package frc.robot.subsystems;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import frc.robot.Constants;

public class Turret {
    public CANSparkMax m_shooterMotorLeft;
    public CANEncoder m_shooterShaftEncoder;
    public CANSparkMax m_shooterMotorRight;
    public SpeedControllerGroup m_shooterMotors;
    public TalonSRX m_turretMotor;
    public TalonSRX m_climberMotor;
    public CANPIDController m_PIDTest;
    public DigitalInput m_limitSwitchLeft;
    public DigitalInput m_limitSwitchRight;
    double lastDirection = -.8;

    public Turret() {
        m_shooterMotorLeft = new CANSparkMax(Constants.shooter.shooterLeftPort, MotorType.kBrushless);
        m_shooterMotorLeft.setSmartCurrentLimit(39);
        m_shooterMotorLeft.setSecondaryCurrentLimit(40);
        m_shooterMotorRight = new CANSparkMax(Constants.shooter.shooterRightPort, MotorType.kBrushless);
        m_shooterMotorRight.setSmartCurrentLimit(39);
        m_shooterMotorRight.setSecondaryCurrentLimit(40);
        m_shooterShaftEncoder = new CANEncoder(m_shooterMotorRight);
        m_shooterMotors = new SpeedControllerGroup(m_shooterMotorLeft, m_shooterMotorRight);
        m_climberMotor = new TalonSRX(Constants.climber.climberMotor1Port);
        m_turretMotor = new TalonSRX(Constants.shooter.turretPort);
        m_PIDTest = new CANPIDController(m_shooterMotorLeft);
        m_PIDTest.setFF(Constants.shooter.shooterFF);
        m_PIDTest.setP(.00001);
        m_limitSwitchLeft = new DigitalInput(0);
        m_limitSwitchRight = new DigitalInput(1);

    }

    public void Turn(double amount) {
        m_turretMotor.set(ControlMode.PercentOutput, amount);
    }

    public void Shoot(double amount) {
        m_shooterMotors.set(amount);
        if(m_shooterShaftEncoder.getVelocity() < 3600)
        {
            System.out.println("Velocity is " + m_shooterShaftEncoder.getVelocity());
         }
    }

    // when amount = 0 then 100% of "to"
    // when amount = 1 then 100% of "from"
    // when amount = 0.5 then 50% of "to" and "from"
    public double lerp(double to, double from, double amount) {
        double result = (1 - amount) * to + amount * from;

        if (result > 1) {
            result = Constants.shooter.turnSpeed; // this is the max speed of the motor
        }
        else if (result < -1) {
            result = -Constants.shooter.turnSpeed; // inverse max speed
        }
        // else if(result < 0.01 || result > -0.01){
        // result = 0; //within threshold
        // }
        // System.out.println("lerp Result is " + result);
        return result;
    }

    public void seek() {

        System.out.println("I am flashBANGED");
        if (m_limitSwitchRight.get() == false) {
            lastDirection = .8;
            Turn(lastDirection);
        } 
        else if (m_limitSwitchLeft.get() == false) {
            lastDirection = -.8;
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

    public boolean rightlimitPressed() {
        if (m_limitSwitchRight.get() == false) {
            return false;
        }
        else {
            return true;
        }
    }

}