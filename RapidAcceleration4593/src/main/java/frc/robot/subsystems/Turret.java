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

    public Intake m_Intake;

    double lastDirection = .8; // may need to switch sign based on starting position

    public Turret() {
        m_shooterMotorLeft = new CANSparkMax(Constants.shooter.shooterLeftPort, MotorType.kBrushless);
        m_shooterMotorLeft.setSmartCurrentLimit(39);
        m_shooterMotorLeft.setSecondaryCurrentLimit(40);
        m_shooterMotorRight = new CANSparkMax(Constants.shooter.shooterRightPort, MotorType.kBrushless);
        m_shooterMotorRight.setSmartCurrentLimit(39);
        m_shooterMotorRight.setSecondaryCurrentLimit(40);
        m_shooterShaftEncoder = new CANEncoder(m_shooterMotorRight);
        m_shooterMotors = new SpeedControllerGroup(m_shooterMotorLeft, m_shooterMotorRight);
        m_shooterMotors.setInverted(true);
        m_turretMotor = new TalonSRX(Constants.shooter.turretPort);
        m_PIDTest = new CANPIDController(m_shooterMotorLeft);
        m_PIDTest.setFF(Constants.shooter.shooterFF);
        m_PIDTest.setP(.00001);
        m_limitSwitchLeft = new DigitalInput(Constants.shooter.leftLimitSwitchPort);
        m_limitSwitchRight = new DigitalInput(Constants.shooter.rightLimitSwitchPort);

        m_Intake = new Intake();
    }

    public void Turn(double amount) {
        m_turretMotor.set(ControlMode.PercentOutput, amount);
    }

    public boolean Shoot(double shooterAmount) { // ended up adding two inputs to the method, in troubleshooting. Not really needed to pass in a hopper speed...
        boolean isToSpeed = false;
        // m_shooterMotors.set(shooterAmount);
        m_shooterMotorLeft.set(-shooterAmount);
        m_shooterMotorRight.set(shooterAmount);
        // System.out.println("shoot called");   
        if (m_shooterShaftEncoder.getVelocity() > 5000) {
            isToSpeed = true;
        }
        // System.out.println("velocity of shooter is:" + m_shooterShaftEncoder.getVelocity());
        return isToSpeed;
        // memememememememememem
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

