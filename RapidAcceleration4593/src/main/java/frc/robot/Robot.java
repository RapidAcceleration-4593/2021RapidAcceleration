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
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANEncoder;

import edu.wpi.first.wpilibj.AnalogInput;
import frc.robot.subsystems.Vision;
import frc.robot.subsystems.DriveTrain;
import frc.robot.Constants;
import frc.robot.subsystems.Turret;
import frc.robot.subsystems.Intake;

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

  public static long m_autoSecs = 0;

  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  public Vision m_vision;
  public DriveTrain m_DriveTrain;
  public Turret m_Turret;
  public Intake m_Intake;
  public TalonSRX m_climberMotor;
  public CANEncoder m_driveEncoder;
  public XboxController m_mainController;
  public XboxController m_auxController; 

  // public DigitalInput m_limitSwitchLeft;
  // public DigitalInput m_limitSwitchRight;

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    m_vision = new Vision();
    m_DriveTrain = new DriveTrain();
    m_Turret = new Turret();
    m_Intake = new Intake();

    m_climberMotor = new TalonSRX(Constants.climber.climberMotor1Port);

    m_mainController = new XboxController(Constants.controllers.mainControllerPort);
    m_auxController = new XboxController(Constants.controllers.auxControllerPort);

    // m_limitSwitchLeft = new DigitalInput(0);
    // m_limitSwitchRight = new DigitalInput(1);
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like diagnostics that you want ran during disabled, autonomous,
   * teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {

  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable chooser
   * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
   * remove all of the chooser code and uncomment the getString line to get the
   * auto name from the text box below the Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure below with additional strings. If using the SendableChooser
   * make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
    m_autoSecs = System.currentTimeMillis() / 1000;
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
      // first we need optimal sensor
      if (m_DriveTrain.readDistance() < Constants.autonomous.optimusRange) {
        m_DriveTrain.drive(-.5, -.5);
        System.out.println(m_DriveTrain.readDistance());
      } else {
        long m_currentSeconds = System.currentTimeMillis() / 1000;
        m_DriveTrain.drive(0, 0);
        if ((m_currentSeconds - m_autoSecs) < 5) {
          track();
          m_DriveTrain.drive(0, 0);
        }
        else {
          m_Intake.liftHopper(0, 0);
          m_Turret.Shoot(0);
          m_DriveTrain.drive(0, 0);
        }
      }
      break;

    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {

    // different methods of driving
    m_DriveTrain.drive(m_mainController.getRawAxis(1), m_mainController.getRawAxis(5));
    // m_DriveTrain.arcadeDrive(m_mainController.getRawAxis(1), m_mainController.getRawAxis(0));
    // System.out.println(m_DriveTrain.encoderValue());

    //bumpers
    if (m_mainController.getBumper(Hand.kRight)) {
      m_Turret.Turn(-1);
    } else if (m_mainController.getBumper(Hand.kLeft)) {
      m_Turret.Turn(1);
    } else {
      m_Turret.Turn(0);
    }

    // shooting manual
    //b
    if (m_auxController.getStartButton()) {
      m_Turret.Shoot(1);
      /// m_Intake.liftHopper(1, .5);
      m_Intake.liftHopper(1, .5);
    } 
    else if (m_auxController.getBButton()) {
      m_Intake.intakeHopper(.3125, .5);
    }
    else if (m_auxController.getXButton()) { // just a smidge
      m_Intake.intakeHopper(0, -.25);
    }
    else {
      m_Turret.Shoot(0);
      m_Intake.liftHopper(0, 0);
      m_Intake.intakeHopper(0, 0);
    }

    //if A is pressed
    track();

    if (m_auxController.getYButton()) {
      m_climberMotor.set(ControlMode.PercentOutput, 1);
    }
    else if (m_auxController.getBackButton()) {
      m_climberMotor.set(ControlMode.PercentOutput, -1);
    }
    else {
      m_climberMotor.set(ControlMode.PercentOutput, 0);
    }
    System.out.println("Current distance: " + m_DriveTrain.readDistance());
    // will eventually put into its own subsystem

}

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }

  public void track()
  {
    // controlling the turret with vision
    // need to stabilize the limelight mount! (mechanical problem)
    //if (m_auxController.getAButton()) {

        m_vision.lightOn();

      if (m_vision.isThereTarget() == 1.0 && 
      (m_Turret.leftLimitPressed() == true && m_Turret.rightlimitPressed() == true)) {

        double lerpResult = m_Turret.lerp(0, m_vision.getAngleX(), 0.2);
        System.out.println("lerp result is: " + lerpResult);
        m_Turret.Turn(-lerpResult);

        // shoot but stop turret
        if (-lerpResult < .5 && -lerpResult > -.5) {
          // m_Turret.Shoot(.75);
          m_Turret.Turn(-lerpResult);
          
          // still increases speed, checks when to activate lift and hopper based on shooter rpm
          if (m_Turret.Shoot(.75)) {
            m_Intake.liftHopper(1, .5); 
          }
          else {
            m_Intake.liftHopper(0, 0);
          }
        }
      
      }
      else if (m_Turret.leftLimitPressed() == false) {
        m_Turret.Turn(0);
        System.out.println("Left limit reached");
      }
      else if (m_Turret.rightlimitPressed() == false) {
        m_Turret.Turn(0);
        System.out.println("Right limit reached");
      }
      else {
        m_Turret.seek();
      }
    //}
    //else {
    //  m_vision.lightOff();
    //}
  }

}
