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
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.AnalogInput;
import frc.robot.subsystems.Vision;
import frc.robot.subsystems.DriveTrain;
import frc.robot.Constants;
import frc.robot.subsystems.Turret;

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

  public Vision m_vision;
  public DriveTrain m_DriveTrain;
  public Turret m_Turret;

  public TalonSRX m_intakeMotor;

  public AnalogInput m_ultrasonic;

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
    
    m_vision = new Vision();
    m_DriveTrain = new DriveTrain();
    m_Turret = new Turret();

    m_ultrasonic = new AnalogInput(0);

    m_intakeMotor = new TalonSRX(6);

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

    // different methods of driving
    m_DriveTrain.drive(m_joystick.getRawAxis(1),m_joystick.getRawAxis(5));
    // m_DriveTrain.arcadeDrive(m_joystick.getRawAxis(1), m_joystick.getRawAxis(0));

    if (m_joystick.getXButton()) {
      m_Turret.Turn(-1);
    }
    else if (m_joystick.getBButton()) {
      m_Turret.Turn(1);
    }
    else {
      m_Turret.Turn(0);
    }

    // shooting 
    if (m_joystick.getYButton()) {
      m_Turret.Shoot(.6); 
    }
    else {
      m_Turret.Shoot(0); 
    }
  
    // controlling the turret with vision 
    // need to stabilize the limelight mount! (mechanical problem) 
    if (m_joystick.getAButton()) {
      if (m_vision.isThereTarget() == 1.0) {

        System.out.println("Seeking");

        double lerpResult = m_Turret.lerp(0, m_vision.getAngleX(), 0.25);
        System.out.println("lerp result is: " + lerpResult);
        m_Turret.Turn(-lerpResult);
        
        //shoot but stop turret
        if (-lerpResult < .4 && -lerpResult > -.4) {
          m_Turret.Shoot(.5);
          m_Turret.Turn(-lerpResult);
        }

        //shoot and move turret
        //if lerpResult == 1
        //only move the turret

      } 
      else {
        System.out.println("I am flashBANGED");
        // eventually will have seeking code here 
      }
    }
    
    double ultrasonicSensorValue = m_ultrasonic.getVoltage();
    final double scaleFactor = 1/(5./1024.);
    double distance = 5 * ultrasonicSensorValue * scaleFactor;
    double convertedValue = distance / 25.4;
    System.out.println("The value of the ultrasonic sensor is: " + convertedValue);

    // will eventually put into its own subsystem
    if (m_joystick.getStartButton()){
      m_intakeMotor.set(ControlMode.PercentOutput, -.5);
    }
    else {
      m_intakeMotor.set(ControlMode.PercentOutput, 0);
    }
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
