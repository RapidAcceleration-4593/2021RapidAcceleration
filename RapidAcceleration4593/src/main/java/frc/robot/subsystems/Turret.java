package frc.robot.subsystems;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANError;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.DigitalInput;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import frc.robot.Constants;

public class Turret {
    public CANSparkMax m_shooterMotorLeft;
    public CANSparkMax m_shooterMotorRight;
    public CANPIDController m_leftPID;
    public CANPIDController m_rightPID;
    public CANEncoder m_shooterLeftEncoder;
    public CANEncoder m_shooterRightEncoder;
    
    // public SpeedControllerGroup m_shooterMotors;
    public TalonSRX m_turretMotor;
    public TalonSRX m_climberMotor;
    
    public DigitalInput m_limitSwitchLeft;
    public DigitalInput m_limitSwitchRight;
    public double m_ffGain = 0.00001;
    public double m_pGain = 0;
    public double m_iGain = 0;
    public double m_dGain = 0;

    double lastDirection = -.8; // may need to switch sign based on starting position

    public Turret() {
        m_shooterMotorLeft = new CANSparkMax(Constants.shooter.shooterLeftPort, MotorType.kBrushless);
        m_shooterMotorRight = new CANSparkMax(Constants.shooter.shooterRightPort, MotorType.kBrushless);
        //m_shooterMotorLeft.setSmartCurrentLimit(39);
        //m_shooterMotorLeft.setSecondaryCurrentLimit(40);
        //m_shooterMotorRight.setSmartCurrentLimit(39);
        //m_shooterMotorRight.setSecondaryCurrentLimit(40);
        m_shooterRightEncoder = new CANEncoder(m_shooterMotorRight);
        m_shooterLeftEncoder = new CANEncoder(m_shooterMotorLeft);
        m_leftPID = m_shooterMotorLeft.getPIDController();
        m_rightPID = m_shooterMotorRight.getPIDController();
        // m_shooterMotors = new SpeedControllerGroup(m_shooterMotorLeft, m_shooterMotorRight);
        // m_shooterMotors.setInverted(true);
        m_turretMotor = new TalonSRX(Constants.shooter.turretPort);
        
        m_limitSwitchLeft = new DigitalInput(Constants.shooter.leftLimitSwitchPort);
        m_limitSwitchRight = new DigitalInput(Constants.shooter.rightLimitSwitchPort);

        m_leftPID.setFF(m_ffGain);
        m_rightPID.setFF(m_ffGain);
        m_leftPID.setP(m_pGain);
        m_rightPID.setP(m_pGain);
        m_leftPID.setI(0);
        m_rightPID.setI(0);
        m_leftPID.setD(m_dGain);
        m_rightPID.setD(m_dGain);
        //m_shooterMotorLeft.burnFlash();
        //m_shooterMotorRight.burnFlash();

    }

    public void tuneFF()
    {
        m_ffGain += 0.00001;
        m_leftPID.setFF(m_ffGain);
        m_rightPID.setFF(m_ffGain);
        //m_shooterMotorLeft.burnFlash();
        //m_shooterMotorRight.burnFlash();
        m_leftPID.setReference(-900, ControlType.kVelocity);
        m_rightPID.setReference(900, ControlType.kVelocity);
        System.out.println("Current left FF: " + m_leftPID.getFF());
        System.out.println("Current right FF: " + m_rightPID.getFF());
        System.out.println("Left Velocity: " + m_shooterLeftEncoder.getVelocity());
        System.out.println("Right Velocity: " + m_shooterRightEncoder.getVelocity());
    }

    public void stopTune()
    {
        m_leftPID.setReference(0, ControlType.kVelocity);
        m_rightPID.setReference(0, ControlType.kVelocity);
    }

    public void tuneP()
    {
        //m_pGain += 0.00001;
        m_leftPID.setP(m_pGain);
        m_rightPID.setP(m_pGain);
        //m_shooterMotorLeft.burnFlash();
        //m_shooterMotorRight.burnFlash();
        m_leftPID.setReference(-900, ControlType.kVelocity);
        m_rightPID.setReference(900, ControlType.kVelocity);
        System.out.println("Current left P: " + m_leftPID.getP());
        System.out.println("Current right P: " + m_rightPID.getP());
        System.out.println("Left Velocity: " + m_shooterLeftEncoder.getVelocity());
        System.out.println("Right Velocity: " + m_shooterRightEncoder.getVelocity());
    }

    public void tuneI()
    {
        m_iGain += 0.00001;
        m_leftPID.setI(m_iGain);
        m_rightPID.setI(m_iGain);
        m_shooterMotorLeft.burnFlash();
        m_shooterMotorRight.burnFlash();
        m_leftPID.setReference(-900, ControlType.kVelocity);
        m_rightPID.setReference(900, ControlType.kVelocity);
        System.out.println("Current I: " + m_iGain);
        System.out.println("Left Velocity: " + m_shooterLeftEncoder.getVelocity());
        System.out.println("Right Velocity: " + m_shooterRightEncoder.getVelocity());
    }

    public void tuneD()
    {
        //m_dGain += 0.00001;
        //m_leftPID.setD(m_dGain);
        //m_rightPID.setD(m_dGain);
        //m_shooterMotorLeft.burnFlash();
        //m_shooterMotorRight.burnFlash();
        m_leftPID.setReference(-900, ControlType.kVelocity);
        m_rightPID.setReference(900, ControlType.kVelocity);
        System.out.println("Current left D: " + m_leftPID.getD());
        System.out.println("Current right D: " + m_rightPID.getD());
        System.out.println("Left Velocity: " + m_shooterLeftEncoder.getVelocity());
        System.out.println("Right Velocity: " + m_shooterRightEncoder.getVelocity());
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
        if (m_shooterLeftEncoder.getVelocity() < -5450 && m_shooterRightEncoder.getVelocity() > 5450) {
            isToSpeed = true;
        }
        //System.out.println("velocity of shooter is:" + m_shooterLeftEncoder.getVelocity());
        //System.out.println("Velocity of the other shooter motor is: " + m_shooterRightEncoder.getVelocity());
        return isToSpeed;
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

