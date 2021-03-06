package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import frc.robot.Constants;

public class Climber {
    public TalonSRX m_climberMotor;
    public TalonSRX m_climberDeployMotor;


public Climber() {
    m_climberMotor = new TalonSRX(Constants.climber.climberMotor1Port);
    m_climberDeployMotor = new TalonSRX(Constants.climber.deployClimberPort);
}

public void climb(double climberSet) {
    m_climberMotor.set(ControlMode.PercentOutput, climberSet);
}

public void deployClimber(double deploySet) {
    m_climberDeployMotor.set(ControlMode.PercentOutput, deploySet);
}

}

