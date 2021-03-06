package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import frc.robot.Constants;

public class WheelOfFortune {
    public TalonSRX m_WoFMotor;


public WheelOfFortune() {
    // m_WoFMotor = new TalonSRX(Constants.wheelOfFortune.wheelOfFortunePort);
}

public void spinDatWheel(double spinSpeed) {
    // m_WoFMotor.set(ControlMode.PercentOutput, spinSpeed);
}

}

