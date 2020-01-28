package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import frc.robot.Constants;

public class Intake {

    public TalonSRX m_hopperMotor;
    public TalonSRX m_intoShooterMotor;
    public TalonSRX m_intakeMotor;
    
    public Intake() {
        m_intakeMotor = new TalonSRX(Constants.intake.intakeMotorPort);
        m_hopperMotor = new TalonSRX(Constants.intake.hopperMotorPort);
        m_intoShooterMotor = new TalonSRX(Constants.intake.intoShooterMotorPort);
    }

    public void intakeHopper(double intakeAmount, double hopperAmount) {
        m_intakeMotor.set(ControlMode.PercentOutput, -intakeAmount);
        m_hopperMotor.set(ControlMode.PercentOutput, -hopperAmount);
    }

    public void liftHopper(double liftAmount, double hopperAmount) {
        m_intoShooterMotor.set(ControlMode.PercentOutput, liftAmount);
        m_hopperMotor.set(ControlMode.PercentOutput, -hopperAmount);
    }
}