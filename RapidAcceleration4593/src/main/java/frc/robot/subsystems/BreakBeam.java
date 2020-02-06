package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DigitalInput;
import frc.robot.Constants;

enum BreakBeamState{
    NotChanging, Changing
}

public class BreakBeam {

    public DigitalInput m_intakeBreakBeam;
    public DigitalInput m_shooterBreakBeam;

    public BreakBeamState m_intakeState;
    public BreakBeamState m_shooterState;

    int m_numBall = 3; 
    
    public BreakBeam() {
        m_intakeBreakBeam = new DigitalInput(Constants.breakBeam.intakeBreakBeamPort);
        m_shooterBreakBeam = new DigitalInput(Constants.breakBeam.shooterBreakBeamPort);
        m_intakeState = BreakBeamState.NotChanging;
        m_shooterState = BreakBeamState.NotChanging;

    }
    public void CheckIntake() {
        if (m_intakeBreakBeam.get() == false && m_intakeState == BreakBeamState.NotChanging) {
                m_intakeState = BreakBeamState.Changing;
        }
        if (m_intakeBreakBeam.get() == true && m_intakeState == BreakBeamState.Changing) {
            m_numBall ++;
            m_intakeState = BreakBeamState.NotChanging;
        }
        System.out.println("# of Ballz: " + m_numBall);
    }   
    public void CheckShooter() {
        if (m_shooterBreakBeam.get() == false && m_shooterState == BreakBeamState.NotChanging) {
                m_shooterState = BreakBeamState.Changing;
        }
        if (m_shooterBreakBeam.get() == true && m_shooterState == BreakBeamState.Changing) {
            m_numBall --;
            if (m_numBall < 0) {
                ++m_numBall;
                System.out.println("you have been v naughty and went negative");
            }
            m_shooterState = BreakBeamState.NotChanging;
        }
        System.out.println("# of Ballz: " + m_numBall);
    } 
}