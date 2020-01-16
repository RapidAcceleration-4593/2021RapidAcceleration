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

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
//import com.revrobotics.CANSparkMax;
//import edu.wpi.first.wpilibj.Joystick;
//import edu.wpi.first.wpilibj.PWMVictorSPX;
//import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.Spark;
import com.revrobotics.*;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import frc.robot.Vision;


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

  //private static final int kMotorPort = 1;
  //private static final int kJoystickPort = 1;

  public CANSparkMax m_motor;
  public CANEncoder m_encoder;
  public CANSparkMax m_motor2;
  public SpeedControllerGroup m_motors;
  public XboxController m_joystick;
  public TalonSRX m_climberMotor;
  public Spark m_sparkPWMTest;
  public Vision m_vision;
  public CANPIDController m_PIDTest;
  
  public DifferentialDrive m_driveTrain;

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
    m_motor = new CANSparkMax(4, MotorType.kBrushless);
    m_motor.setSmartCurrentLimit(39);
    m_motor.setSecondaryCurrentLimit(40);

    m_motor2 = new CANSparkMax(5, MotorType.kBrushless);
    m_motor2.setSmartCurrentLimit(39);
    m_motor2.setSecondaryCurrentLimit(40);
    m_encoder = new CANEncoder(m_motor);
    m_motors = new SpeedControllerGroup(m_motor, m_motor2);
    m_climberMotor = new TalonSRX(1);
    m_sparkPWMTest = new Spark(0);
    m_vision = new Vision();
    m_PIDTest = new CANPIDController(m_motor);

    m_PIDTest.setFF(.00001);
    // m_PIDTest.setP(.00001);

    m_joystick = new XboxController(0);

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
        break;
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    
    if (m_joystick.getXButton()) {
      // System.out.println("The talon is running");
      m_climberMotor.set(ControlMode.PercentOutput, 1);
    }
    else {
      m_climberMotor.set(ControlMode.PercentOutput, 0);
    }

    if (m_joystick.getYButton()) {
      // System.out.println("The spark is running");
      // System.out.println("Velocity is " + m_encoder.getVelocity());
      m_motors.set(-1);    
    }
    else {
      m_motors.set(0);
      // System.out.println("Not Running!!");
      // System.out.println("Velocity is " + m_encoder.getVelocity());
    }
   
    // System.out.println("tx: " + m_vision.getAngleX());
    // System.out.println("ty: " + m_vision.getAngleY());
  

  // if (m_joystick.getAButton()) {
    //must indent again with button added
  if (m_vision.getAngleX() < -2.5) {
     System.out.println("We move left");
    }
   if (m_vision.getAngleX() > 2.5) {
     System.out.println("We move right");
    }
   if (m_vision.getAngleX() < 2.5 && m_vision.getAngleX() > -2.5) {
     System.out.println("We shoot!!!!");
     m_motors.set(-.75);
    }

  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
