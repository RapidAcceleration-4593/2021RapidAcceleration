/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.revrobotics.AlternateEncoderType;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.EncoderType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.CAN;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.XboxController;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import frc.robot.Vision;
import frc.robot.Constants;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  public CANSparkMax m_shooterMotorLeft;
  public CANEncoder m_shooterShaftEncoder;
  public CANSparkMax m_shooterMotorRight;
  public SpeedControllerGroup m_shooterMotors;
  public TalonSRX m_turretMotor;
  public TalonSRX m_climberMotor;
  
  public CANSparkMax FRM;
  public CANSparkMax RRM;
  public CANSparkMax FLM;
  public CANSparkMax RLM;
  public SpeedControllerGroup m_rightDrive;
  public SpeedControllerGroup m_leftDrive;
  public DifferentialDrive m_driveTrain;

  public Vision m_vision;
  public CANPIDController m_PIDTest;
  public CANPIDController m_rightSidePID;
  public CANPIDController m_leftSidePID;
  public CANEncoder m_rightSideEncoder;
  public CANEncoder m_leftSideEncoder;

  public XboxController m_joystick;

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    System.out.println("Init");
    
    m_shooterMotorLeft = new CANSparkMax(Constants.shooter.shooterLeftPort, MotorType.kBrushless);
    m_shooterMotorLeft.setSmartCurrentLimit(39);
    m_shooterMotorLeft.setSecondaryCurrentLimit(40);
    m_shooterMotorRight = new CANSparkMax(Constants.shooter.shooterRightPort, MotorType.kBrushless);
    m_shooterMotorRight.setSmartCurrentLimit(39);
    m_shooterMotorRight.setSecondaryCurrentLimit(40);
    m_shooterShaftEncoder = new CANEncoder(m_shooterMotorRight);
    m_shooterMotors = new SpeedControllerGroup(m_shooterMotorLeft, m_shooterMotorRight);
    m_climberMotor = new TalonSRX(Constants.climber.climberMotor1Port);

    m_turretMotor = new TalonSRX(Constants.shooter.turretPort);
    m_vision = new Vision();
    m_PIDTest = new CANPIDController(m_shooterMotorLeft);

    m_PIDTest.setFF(Constants.shooter.shooterFF);
    m_PIDTest.setP(.00001);

    
    FRM = new CANSparkMax(Constants.driveTrain.FRMPort, MotorType.kBrushless);
    RRM = new CANSparkMax(Constants.driveTrain.RRMPort, MotorType.kBrushless);
    m_rightDrive = new SpeedControllerGroup(FRM, RRM);
    RLM = new CANSparkMax(Constants.driveTrain.RLMPort, MotorType.kBrushless);
    FLM = new CANSparkMax(Constants.driveTrain.FLMPort, MotorType.kBrushless);
    m_leftDrive = new SpeedControllerGroup(FLM, RLM);
    m_rightSideEncoder = new CANEncoder(FRM);
    m_rightSidePID = new CANPIDController(FRM);
    m_leftSidePID = new CANPIDController(FLM);
    m_leftSideEncoder = new CANEncoder(FLM);
    m_driveTrain = new DifferentialDrive(m_leftDrive, m_rightDrive);
    m_driveTrain.setRightSideInverted(true);
    
    m_leftSidePID.setOutputRange(-1, 1);
    m_leftSidePID.setFF(.00015);
    m_leftSidePID.setP(.00035);
    m_leftSidePID.setI(0);
    m_leftSidePID.setD(0);

    m_rightSidePID.setOutputRange(-1, 1);
    m_rightSidePID.setFF(.00015);
    m_rightSidePID.setP(.00035);
    m_rightSidePID.setI(0);
    m_rightSidePID.setD(0);

    m_joystick = new XboxController(Constants.controllers.controllerOnePort);

  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {

  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        System.out.println("we are in auto");
        break;
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {

    // comment one of these drivetrains out or bad things will happen
   m_driveTrain.tankDrive(-m_joystick.getRawAxis(1), -m_joystick.getRawAxis(5));
    // m_driveTrain.arcadeDrive(m_joystick.getRawAxis(1), m_joystick.getRawAxis(0));

    // System.out.println("Right side RPM = " + m_rightSideEncoder.getVelocity());
    // System.out.println("Left side RPM = " + m_leftSideEncoder.getVelocity());

    if (m_joystick.getXButton()) {
      // System.out.println("Tlhe talon is running");
      m_turretMotor.set(ControlMode.PercentOutput, -1);
    }
    else if (m_joystick.getBButton()) {
      m_turretMotor.set(ControlMode.PercentOutput, 1);
    }
    else {
      m_turretMotor.set(ControlMode.PercentOutput, 0);
    }

    if (m_joystick.getYButton()) {
      // System.out.println("The spark is running");
      System.out.println("Velocity is " + m_shooterShaftEncoder.getVelocity());
      m_shooterMotors.set(1);    
    }
    else {
      m_shooterMotors.set(0);
      // System.out.println("Not Running!!");
      // System.out.println("Velocity is " + m_encoder.getVelocity());
    }
   
    // System.out.println("tx: " + m_vision.getAngleX());
    // System.out.println("ty: " + m_vision.getAngleY());
  

    if (m_joystick.getAButton()) {

      if (m_vision.isThereTarget() == 1.0) {

        System.out.println("Seeking");

        // don't need to move
        if (m_vision.getAngleX() < Constants.shooter.threshold && m_vision.getAngleX() > -Constants.shooter.threshold) {
          System.out.println("We shoot!!!!");
          m_shooterMotors.set(.5);
          m_turretMotor.set(ControlMode.PercentOutput, 0);
        } else { // need to move
          if (m_vision.getAngleX() < -Constants.shooter.threshold) {
            System.out.println("We move left");
            m_turretMotor.set(ControlMode.PercentOutput, .75);
          }
          if (m_vision.getAngleX() > Constants.shooter.threshold) {
            System.out.println("We move right");
            m_turretMotor.set(ControlMode.PercentOutput, -.75);
          }
        }
      } else {
        System.out.println("I am flashBANGED");
      }
    }
    

  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
