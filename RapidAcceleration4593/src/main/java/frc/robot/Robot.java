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
import edu.wpi.first.wpilibj.DigitalInput;

import com.revrobotics.CANEncoder;

import frc.robot.subsystems.Vision;
import frc.robot.subsystems.WheelOfFortune;
import frc.robot.subsystems.DriveTrain;
import frc.robot.Constants;
import frc.robot.subsystems.Turret;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Climber;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */

public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "Simple";
  // private String m_autoSelected;

  public static long m_autoSecs = 0;

  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  public Vision m_vision;
  public DriveTrain m_DriveTrain;
  public Turret m_Turret;
  public Intake m_Intake;
  public WheelOfFortune m_WoF;
  public Climber m_Climber;
  public CANEncoder m_driveEncoder;
  public XboxController m_mainController;
  public XboxController m_auxController;

  public DigitalInput m_limitSwitchLeft;
  public DigitalInput m_limitSwitchRight;

  public boolean m_hasFirstStopped = false;

  long runningTime = System.currentTimeMillis();
  long firstStopTime;
  double m_curStopSpeed = 0;

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("Simple Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    m_vision = new Vision();
    m_DriveTrain = new DriveTrain();
    m_Turret = new Turret();
    m_Intake = new Intake();
    m_WoF = new WheelOfFortune();
    m_Climber = new Climber();

    m_mainController = new XboxController(Constants.controllers.mainControllerPort);
    m_auxController = new XboxController(Constants.controllers.auxControllerPort);

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
    // m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    // System.out.println("Auto selected: " + m_autoSelected);
    m_autoSecs = System.currentTimeMillis() / 1000;
    m_DriveTrain.zeroEncoder();
    m_DriveTrain.brakeMode();
  }

  /**
   * This function is called periodically during autonomous. Add states for: 1)
   * Initial back and shoot (initial) 2) pick up ammo (after break beam reads 0 or
   * after x time: change state) 3) shoot again (after ammo pick up and break beam
   * > 0 or after x time)
   */

  @Override
  public void autonomousPeriodic() {
    runningTime = System.currentTimeMillis();
    String m_autoSelected = "Simple"; // Simple or Default
    switch (m_autoSelected) {
    case kCustomAuto:
      if (m_DriveTrain.encoderValue() < Constants.autonomous.firstBackupStop) {
        // m_DriveTrain.m_leftSidePID.setReference(.7 * Constants.driveTrain.maxRPM,
        // ControlType.kVelocity);
        // m_DriveTrain.m_rightSidePID.setReference(-.7 * Constants.driveTrain.maxRPM,
        // ControlType.kVelocity);
        // m_DriveTrain.drive(-.52, -.5);
        // m_DriveTrain.arcadeDrive(-.5, 0);
        m_DriveTrain.arcadeDrive(-.5, 0);
      } else if (m_DriveTrain.encoderValue() > Constants.autonomous.firstBackupStop) {
        // m_DriveTrain.m_leftSidePID.setReference(0, ControlType.kVelocity);
        // m_DriveTrain.m_rightSidePID.setReference(0, ControlType.kVelocity);
        // m_DriveTrain.drive(0, 0);
        m_DriveTrain.arcadeDrive(0, 0);
        track();
      } else {
       // the bigger the amount the slower the slow
        m_curStopSpeed = m_Turret.stopShooter(0, m_curStopSpeed, .8);
        m_Intake.liftHopper(0, 0, true, runningTime);
        m_vision.lightOff();
        m_Turret.Turn(0);
        m_DriveTrain.arcadeDrive(0, .69);
      }
      break;
    case kDefaultAuto:
    default:
      // Put default auto code here
      // first we need optimal sensor
      // m_breakBeamZ.CheckIntake();

      if (m_DriveTrain.encoderValue() < Constants.autonomous.firstBackupStop) {
        // m_DriveTrain.drive(-.442, -.425);
        // System.out.println("Moving to first stop.");
        m_DriveTrain.arcadeDrive(-.5, 0);
      } else if (m_DriveTrain.encoderValue() >= Constants.autonomous.firstBackupStop && m_hasFirstStopped == false) {
        // m_DriveTrain.drive(0, 0);
        m_DriveTrain.arcadeDrive(0, 0);
        m_hasFirstStopped = true;
        // System.out.println("First stop.");
        firstStopTime = System.currentTimeMillis();
      } else if (m_hasFirstStopped == true && m_DriveTrain.encoderValue() < Constants.autonomous.encoderBackUp
          && (runningTime - firstStopTime > 500)) {
        // m_DriveTrain.drive(-.442, -.425);
        // m_DriveTrain.m_leftSidePID.setReference(-.75 * Constants.driveTrain.maxRPM,
        // ControlType.kVelocity);
        // m_DriveTrain.m_rightSidePID.setReference(-.75 * Constants.driveTrain.maxRPM,
        // ControlType.kVelocity);
        m_DriveTrain.arcadeDrive(-.5, 0);
        m_Intake.intakeHopper(.775, .5);
        m_Intake.liftHopper(0, .5, true, runningTime);
        // System.out.println("Moving to final stop.");
      } else {
        // m_DriveTrain.drive(0, 0);
        // System.out.println("Final stop.");
        m_DriveTrain.arcadeDrive(0, 0);
        m_curStopSpeed = m_Turret.stopShooter(0, m_curStopSpeed, .8);
        m_Intake.intakeHopper(0, 0);
        m_Intake.liftHopper(0, 0, true, runningTime);
      }
      break;
    }
  }

  /**
   * This function is called periodically during operator control.
   */

  @Override
  public void teleopInit() {
    m_DriveTrain.coastMode();
  }

  @Override
  public void teleopPeriodic() {
    runningTime = System.currentTimeMillis();

    m_DriveTrain.arcadeDrive(m_mainController.getRawAxis(1), .75 * m_mainController.getRawAxis(4));
    //m_DriveTrain.driveStraight(m_mainController.getRawAxis(1));
    // main controller
    if (m_mainController.getBumper(Hand.kRight) && m_Turret.rightLimitPressed() == true) {
      m_Turret.Turn(-1);
    } else if (m_mainController.getBumper(Hand.kLeft) && m_Turret.leftLimitPressed() == true) {
      m_Turret.Turn(1);
    } else if (m_mainController.getAButton()) {
      m_Intake.liftHopper(1, 0, true, runningTime);
    } else if (m_mainController.getXButton()) {
      m_Intake.liftHopper(-.5, 0, true, runningTime);
    } else {
      m_Turret.Turn(0);
    }

    // aux controller turret stuffs
    /*if (m_auxController.getBackButton()) {
      boolean canShoot = m_Turret.Shoot(1);
      if(canShoot){
        m_curStopSpeed = 1;
      }
    */
    if (m_auxController.getAButton()) {
      m_Intake.intakeHopper(.69, .3);
    } else if (m_auxController.getBButton()) { // just a smidge
      m_Intake.intakeHopper(-1, -.5);
    } else if (m_auxController.getYButton()) { // must be placed here to keep hopper running, otherwise it sets to 0
                                               // when the shoot method is called
      // track();

      
      m_vision.lightOn();

      if (m_vision.isThereTarget() == 1.0
          && (m_Turret.leftLimitPressed() == true && m_Turret.rightLimitPressed() == true)) {
  
        double lerpResult = m_Turret.lerp(-.25, m_vision.getAngleX(), 0.15);
        // System.out.println("lerp result is: " + lerpResult);
        m_Turret.Turn(-lerpResult);
  
        // shoot but stop turret
        if (-lerpResult < .5 && -lerpResult > -.5) {
          // m_Turret.Shoot(.75);
          m_Turret.Turn(-lerpResult);
          System.out.println("Ready to shoot");
        }
        else {
          System.out.println("trying to lock");
          m_Turret.Turn(-lerpResult);
        }
      }
      else if (m_vision.isThereTarget() != 1.0) {
        m_Turret.seek();
      }
      
      

    } else if (m_auxController.getXButton()) {
      m_Intake.intakeHopper(.69, 0);
    } else {
      m_curStopSpeed = m_Turret.stopShooter(0, m_curStopSpeed, .8);
      m_Intake.liftHopper(0, 0, true, runningTime);
      m_Intake.intakeHopper(0, 0);
      m_vision.lightOff();
    }

    // aux controller climb stuffs
    if (m_auxController.getBumper(Hand.kLeft)) {
      m_Climber.deployClimber(-.35);
    } else if (m_auxController.getBumper(Hand.kRight)) {
      m_Climber.climb(1);
    } else if (m_auxController.getStartButton()) {
      m_Climber.climb(-1);
    } else {
      m_Climber.climb(0);
      m_Climber.deployClimber(0);
    }

    // auz controller spin DAT wheel stuff
    if (m_auxController.getTriggerAxis(Hand.kRight) >= .1) {
      m_WoF.spinDatWheel(.4593 * m_auxController.getTriggerAxis(Hand.kRight));
    } else if (m_auxController.getTriggerAxis(Hand.kLeft) >= .1) {
      m_WoF.spinDatWheel(-.4593 * m_auxController.getTriggerAxis(Hand.kLeft));
    } else {
      m_WoF.spinDatWheel(0);
    }

    if (m_auxController.getBackButton()) {
      boolean canShoot = m_Turret.Shoot(1);
        if(canShoot){
          m_curStopSpeed = 1;
        }
        if (canShoot) { // && m_breakBeamZ.m_shooterState == BreakBeam.BreakBeamState.NotChanging
          m_Intake.liftHopper(-1, .75, true, runningTime); // byepass false, hopper at .75

        } else {
          m_Intake.liftHopper(0, 0, true, runningTime);
        }
      }

  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }

  public void track() {
    // controlling the turret with vision
    // need to stabilize the limelight mount! (mechanical problem)
    // if (m_auxController.getAButton()) {

    m_vision.lightOn();

    if (m_vision.isThereTarget() == 1.0
        && (m_Turret.leftLimitPressed() == true && m_Turret.rightLimitPressed() == true)) {

      double lerpResult = m_Turret.lerp(-.25, m_vision.getAngleX(), 0.15);
      // System.out.println("lerp result is: " + lerpResult);
      m_Turret.Turn(-lerpResult);

      // shoot but stop turret
      if (-lerpResult < .5 && -lerpResult > -.5) {
        // m_Turret.Shoot(.75);
        m_Turret.Turn(-lerpResult);

        // still increases speed, checks when to activate lift and hopper based on
        // shooter 
        boolean canShoot = m_Turret.Shoot(1);
        if(canShoot){
          m_curStopSpeed = 1;
        }
        if (canShoot) { // && m_breakBeamZ.m_shooterState == BreakBeam.BreakBeamState.NotChanging
          m_Intake.liftHopper(-1, .75, true, runningTime); // byepass false, hopper at .75

        } else {
          m_Intake.liftHopper(0, 0, true, runningTime);
        }
      }
    } else if (m_Turret.leftLimitPressed() == false) {
      // m_Turret.Turn(0);
      // System.out.println("Left limit reached");
      m_Turret.seek();
    } else if (m_Turret.rightLimitPressed() == false) {
      // m_Turret.Turn(0);
      // System.out.println("Right limit reached");
      m_Turret.seek();
    } else {
      m_Turret.seek();
    }
    // }
    // else {
    // m_vision.lightOff();
    // }
  }

}
