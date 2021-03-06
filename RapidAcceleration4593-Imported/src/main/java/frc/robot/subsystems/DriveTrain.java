package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import com.revrobotics.CANPIDController;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.revrobotics.CANEncoder;
import frc.robot.Constants;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.AnalogInput;

public class DriveTrain{
   
    public CANSparkMax FRM;
    public CANSparkMax RRM;
    public CANSparkMax FLM;
    public CANSparkMax RLM;
    public SpeedControllerGroup m_rightDrive;
    public SpeedControllerGroup m_leftDrive;
    public DifferentialDrive m_driveTrain;
    public CANPIDController m_frontRightSidePID; 
    public CANPIDController m_frontLeftSidePID; 
    public CANPIDController m_rightSidePID;
    public CANPIDController m_leftSidePID;
    public CANEncoder m_frontRightSideEncoder;
    public CANEncoder m_frontLeftSideEncoder;
    public CANEncoder m_rightSideEncoder;
    public CANEncoder m_leftSideEncoder;

    double rotations = 0;

    double m_ffGain = 0.000015;
    double m_pGain = 6e-5;
    double m_iGain = 0;
    double m_IzGain = 0;
    double m_dGain = 0;
    double m_minOutput = -1;
    double m_maxOutput = 1;
    double m_maxRPM = 5700;

    public DriveTrain (){
        //right motors
        FRM = new CANSparkMax(Constants.driveTrain.FRMPort, MotorType.kBrushless);
        RRM = new CANSparkMax(Constants.driveTrain.RRMPort, MotorType.kBrushless);
        //left motors
        FLM = new CANSparkMax(Constants.driveTrain.FLMPort, MotorType.kBrushless);
        RLM = new CANSparkMax(Constants.driveTrain.RLMPort, MotorType.kBrushless);

        m_rightSideEncoder = RRM.getEncoder();
        m_leftSideEncoder = RLM.getEncoder();
        m_frontRightSideEncoder = FRM.getEncoder();
        m_frontLeftSideEncoder = FLM.getEncoder();
        FRM.restoreFactoryDefaults();
        RRM.restoreFactoryDefaults();
        RLM.restoreFactoryDefaults();
        FLM.restoreFactoryDefaults();

        m_rightSidePID = RRM.getPIDController();
        m_leftSidePID = RLM.getPIDController();        
        m_frontRightSidePID = FRM.getPIDController();
        m_frontLeftSidePID = FLM.getPIDController();

        m_leftSidePID.setFF(m_ffGain);
        m_rightSidePID.setFF(m_ffGain);
        m_leftSidePID.setP(m_pGain);
        m_rightSidePID.setP(m_pGain);
        m_leftSidePID.setI(m_iGain);
        m_rightSidePID.setI(m_iGain);
        m_leftSidePID.setD(m_dGain);
        m_rightSidePID.setD(m_dGain);
        m_leftSidePID.setIZone(m_IzGain);
        m_rightSidePID.setIZone(m_IzGain);
        m_leftSidePID.setOutputRange(m_minOutput, m_maxOutput);
        m_rightSidePID.setOutputRange(m_minOutput, m_maxOutput);

        m_frontLeftSidePID.setFF(m_ffGain);
        m_frontRightSidePID.setFF(m_ffGain);
        m_frontLeftSidePID.setP(m_pGain);
        m_frontRightSidePID.setP(m_pGain);
        m_frontLeftSidePID.setI(m_iGain);
        m_frontRightSidePID.setI(m_iGain);
        m_frontLeftSidePID.setD(m_dGain);
        m_frontRightSidePID.setD(m_dGain);
        m_frontLeftSidePID.setIZone(m_IzGain);
        m_frontRightSidePID.setIZone(m_IzGain);
        m_frontLeftSidePID.setOutputRange(m_minOutput, m_maxOutput);
        m_frontRightSidePID.setOutputRange(m_minOutput, m_maxOutput);

        m_rightDrive = new SpeedControllerGroup(FRM, RRM);
        m_leftDrive = new SpeedControllerGroup(FLM, RLM);

        m_driveTrain = new DifferentialDrive(m_leftDrive, m_rightDrive);
        m_driveTrain.setRightSideInverted(true);
        m_driveTrain.setDeadband(.1);

    }

    public void drive(double a1, double a2){
        // comment one of these drivetrains out or bad things will happen
        // m_leftSidePID.setReference(a1, ControlType.kVelocity);
        // m_rightSidePID.setReference(a2, ControlType.kVelocity);
        m_driveTrain.tankDrive(-a1, -a2);

    }

    public void arcadeDrive (double a1, double a2) {
        m_driveTrain.arcadeDrive(-a1, a2);
    }

    public void driveStraight (double a1) {
        double error = Math.abs(m_rightSideEncoder.getVelocity() + m_frontRightSideEncoder.getVelocity()) - Math.abs(m_leftSideEncoder.getVelocity() + m_frontLeftSideEncoder.getVelocity()); 
        double turn_power = m_pGain * error; 
        m_driveTrain.arcadeDrive(a1, turn_power, false);
        System.out.println("error is: " + error);
        System.out.println("Turn Power is: " + turn_power);
    }    
    
    public double encoderValue() {
        rotations = Math.abs((m_leftSideEncoder.getPosition()) + Math.abs((m_rightSideEncoder.getPosition()))) / 2;
        System.out.println("Encoder position is: " + m_leftSideEncoder.getPosition());
        return rotations;
    }

    public void zeroEncoder() {
        m_leftSideEncoder.setPosition(0);
        m_rightSideEncoder.setPosition(0);
    }

    public void brakeMode() {
        FLM.setIdleMode(IdleMode.kBrake);
        FRM.setIdleMode(IdleMode.kBrake);
        RRM.setIdleMode(IdleMode.kBrake);
        RLM.setIdleMode(IdleMode.kBrake);
    }

    public void coastMode() {
        FLM.setIdleMode(IdleMode.kCoast);
        FRM.setIdleMode(IdleMode.kCoast);
        RRM.setIdleMode(IdleMode.kCoast);
        RLM.setIdleMode(IdleMode.kCoast);
    }
}