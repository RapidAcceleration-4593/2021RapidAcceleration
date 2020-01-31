package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import com.revrobotics.CANPIDController;
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
    public CANPIDController m_rightSidePID;
    public CANPIDController m_leftSidePID;
    public CANEncoder m_rightSideEncoder;
    public CANEncoder m_leftSideEncoder;

    public AnalogInput m_ultrasonic;

    double rotations = 0;


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
        m_driveTrain.setDeadband(.03);
        
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

        // FRM.burnFlash();
        // FLM.burnFlash();

        m_leftSideEncoder.setPosition(0);
        m_ultrasonic = new AnalogInput(Constants.driveTrain.ultrasonicPort);
    }

    public void drive(double a1, double a2){
        // comment one of these drivetrains out or bad things will happen
        m_driveTrain.tankDrive(-a1, -a2);
    }

    public void arcadeDrive (double a1, double a2) {
        m_driveTrain.arcadeDrive(-a1, a2);
    }
    
    public double encoderValue() {
        System.out.println("Encoder position is: " + m_leftSideEncoder.getPosition());
        rotations = m_leftSideEncoder.getPosition();
        return rotations;
    }

    public double readDistance() {
        double ultrasonicSensorValue = m_ultrasonic.getVoltage();
        final double scaleFactor = 1 / (5. / 1024.);
        double distance = 5 * ultrasonicSensorValue * scaleFactor;
        double convertedValue = distance / (305);

        return convertedValue;
    }
}