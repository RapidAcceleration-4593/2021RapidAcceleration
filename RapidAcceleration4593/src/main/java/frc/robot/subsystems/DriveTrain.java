package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.*;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;

import com.revrobotics.CANPIDController;

import com.revrobotics.CANEncoder;

import frc.robot.Constants;

import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class DriveTrain{
   
    public CANSparkMax FRM;
    public CANSparkMax RRM;
    public CANSparkMax FLM;
    public CANSparkMax RLM;
    public SpeedControllerGroup m_rightDrive;
    public SpeedControllerGroup m_leftDrive;
    public DifferentialDrive m_driveTrain;
    public CANPIDController m_rightSidePID;
    public CANPIDController m_leftSidePID;
    public CANEncoder m_rightSideEncoder;
    public CANEncoder m_leftSideEncoder;


    public DriveTrain (){
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
    }

    public void drive(double a1, double a2){
        // comment one of these drivetrains out or bad things will happen
        m_driveTrain.tankDrive(-a1, -a2);
        
        //the below code is for a different mode...
        //will need to another function and called from the other mode.
        // m_driveTrain.arcadeDrive(m_joystick.getRawAxis(1), m_joystick.getRawAxis(0));
    } 

}