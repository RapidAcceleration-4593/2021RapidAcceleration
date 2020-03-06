package frc.robot.subsystems;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.DigitalInput;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import frc.robot.Constants;

public class Turret {
    CANSparkMax m_shooterMotorLeft;
    CANSparkMax m_shooterMotorRight;
    CANPIDController m_leftPID;
    CANPIDController m_rightPID;
    CANEncoder m_shooterLeftEncoder;
    CANEncoder m_shooterRightEncoder;
    
    TalonSRX m_turretMotor;
    TalonSRX m_climberMotor;
    
    DigitalInput m_limitSwitchLeft;
    DigitalInput m_limitSwitchRight;
    double m_ffGain = 0.000015;
    double m_pGain = 6e-5;
    double m_iGain = 0;
    double m_IzGain = 0;
    double m_dGain = 0;
    double m_minOutput = -1;
    double m_maxOutput = 1;
    double m_maxRPM = 5700;

    double lastDirection = -.8; // may need to switch sign based on starting position

    public Turret() {
        m_shooterMotorLeft = new CANSparkMax(Constants.shooter.shooterLeftPort, MotorType.kBrushless);
        m_shooterMotorRight = new CANSparkMax(Constants.shooter.shooterRightPort, MotorType.kBrushless);

        m_shooterMotorLeft.restoreFactoryDefaults();
        m_shooterMotorRight.restoreFactoryDefaults();

        m_shooterRightEncoder = m_shooterMotorRight.getEncoder();
        m_shooterLeftEncoder = m_shooterMotorLeft.getEncoder();
        m_leftPID = m_shooterMotorLeft.getPIDController();
        m_rightPID = m_shooterMotorRight.getPIDController();

        m_turretMotor = new TalonSRX(Constants.shooter.turretPort);
        
        m_limitSwitchLeft = new DigitalInput(Constants.shooter.leftLimitSwitchPort);
        m_limitSwitchRight = new DigitalInput(Constants.shooter.rightLimitSwitchPort);

        m_leftPID.setFF(m_ffGain);
        m_rightPID.setFF(m_ffGain);
        m_leftPID.setP(m_pGain);
        m_rightPID.setP(m_pGain);
        m_leftPID.setI(m_iGain);
        m_rightPID.setI(m_iGain);
        m_leftPID.setD(m_dGain);
        m_rightPID.setD(m_dGain);
        m_leftPID.setIZone(m_IzGain);
        m_rightPID.setIZone(m_IzGain);
        m_leftPID.setOutputRange(m_minOutput, m_maxOutput);
        m_rightPID.setOutputRange(m_minOutput, m_maxOutput);

    }

    public void Turn(double amount) {
        m_turretMotor.set(ControlMode.PercentOutput, amount);
    }

    public boolean Shoot(double shooterAmount) { // ended up adding two inputs to the method, in troubleshooting. Not really needed to pass in a hopper speed...
        boolean isToSpeed = false;
        m_rightPID.setReference(shooterAmount * 3.5 * m_maxRPM, ControlType.kVelocity);
        m_leftPID.setReference(shooterAmount * 3.5 * -m_maxRPM, ControlType.kVelocity);
        if (m_shooterLeftEncoder.getVelocity() < -4500 && m_shooterRightEncoder.getVelocity() > 4500) {
            isToSpeed = true;
        }
        return isToSpeed;
    }

    public double stopShooter(double to, double from, double amount)
    {
        double shooterLerp = lerpResult(to, from, amount);

        if (shooterLerp <= .001){
            shooterLerp = 0;
        }
        m_rightPID.setReference(shooterLerp * 3.5 * m_maxRPM, ControlType.kVelocity);
        m_leftPID.setReference(shooterLerp * 3.5 * -m_maxRPM, ControlType.kVelocity);
        return shooterLerp;
    }


    public double lerpResult(double to, double from, double amount)
    {
        double result = (1 - amount) * to + amount * from;
        return result;
    }
    // when amount = 0 then 100% of "to"
    // when amount = 1 then 100% of "from"
    // when amount = 0.5 then 50% of "to" and "from"
    public double lerp(double to, double from, double amount) {
        double result = lerpResult(to, from, amount);

        if (result > 1) {
            result = Constants.shooter.turnSpeed; // this is the max speed of the motor
        }
        else if (result < -1) {
            result = -Constants.shooter.turnSpeed; // inverse max speed
        }

        return result;
    }

    public void seek() {

        //System.out.println("I am flashBANGED");
        if (m_limitSwitchLeft.get() == false) {
            lastDirection = -.8;
            Turn(lastDirection);
        } 
        else if (m_limitSwitchRight.get() == false) {
            lastDirection = .8;
            Turn(lastDirection);
        } 
        else {
            Turn(lastDirection);
        }
    }

    public boolean leftLimitPressed() {
        if (m_limitSwitchLeft.get() == false) {
            return false;
        }
        else {
            return true;
        }
    }

    public boolean rightLimitPressed() {
        if (m_limitSwitchRight.get() == false) {
            return false;
        }
        else {
            return true;
        }
    }

}

