package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import frc.robot.Constants;

public class Intake {

    public TalonSRX m_hopperMotor;
    public TalonSRX m_intoShooterMotor;
    public TalonSRX m_intakeMotor;
    
    public long switchPressedTime = 0;
    public DigitalInput m_liftLimitSwitch;
    public Intake() {

        m_intakeMotor = new TalonSRX(Constants.intake.intakeMotorPort);
        m_hopperMotor = new TalonSRX(Constants.intake.hopperMotorPort);
        m_intoShooterMotor = new TalonSRX(Constants.intake.intoShooterMotorPort);
        m_liftLimitSwitch = new DigitalInput(Constants.intake.backLimitSwitchPort);
    }
    
    public void intakeHopper(double intakeAmount, double hopperAmount) {
        m_intakeMotor.set(ControlMode.PercentOutput, -intakeAmount);
        m_hopperMotor.set(ControlMode.PercentOutput, hopperAmount);
    }

    public void liftHopper(double liftAmount, double hopperAmount, boolean bypass, long runningTime) {

        if (bypass == true) {
            m_intoShooterMotor.set(ControlMode.PercentOutput, liftAmount);
            m_hopperMotor.set(ControlMode.PercentOutput, hopperAmount);
        }
        else {
            if (m_liftLimitSwitch.get() == true &&
            (runningTime - switchPressedTime <= 2500 || switchPressedTime == 0))
            {
                if(switchPressedTime == 0)
                {
                    switchPressedTime = System.currentTimeMillis();
                }

                System.out.println(m_liftLimitSwitch.get());
                
                m_intoShooterMotor.set(ControlMode.PercentOutput, liftAmount);
                m_hopperMotor.set(ControlMode.PercentOutput, hopperAmount);
            }
            else{
                switchPressedTime = 0;
                m_intoShooterMotor.set(ControlMode.PercentOutput, 0);
                m_hopperMotor.set(ControlMode.PercentOutput, 0);
            }
            
        }

    }

}