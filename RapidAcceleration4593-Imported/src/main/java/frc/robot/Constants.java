package frc.robot;

public class Constants {
    public class driveTrain {
        public static final int FRMPort = 8;
        public static final int RRMPort = 9;
        public static final int FLMPort = 10;
        public static final int RLMPort = 11;

        public static final double maxRPM = 5500;

        // public static final int ultrasonicPort = 1;
    }

    public class shooter {
        public static final int shooterLeftPort = 12;
        public static final int shooterRightPort = 13;

        public static final int turretPort = 7;

        public static final int leftLimitSwitchPort = 7;
        public static final int rightLimitSwitchPort = 5;

        public static final double shooterFF = .00001;

        public static final double threshold = .5;

        public static final double turnSpeed = .5;

    }

    public class intake {
        public static final int intakeMotorPort = 1;
        public static final int hopperMotorPort = 2;
        public static final int intoShooterMotorPort = 6;
        public static final int bottomHopperMotorPort = 3;

        public static final int backLimitSwitchPort = 6;
        public static final int backBreakBeam = 2;
    }

    public class climber {
        public static final int climberMotor1Port = 4;
        public static final int deployClimberPort = 5;
    }

    public class breakBeam {
        // public static final int intakeBreakBeamPort = 2;
        public static final int shooterBreakBeamPort = 4; 
    }

    public class controllers {
        public static final int mainControllerPort = 0;
        public static final int auxControllerPort = 1;
    }

    public class autonomous {
        public static final double optimusRange = 10.5;
        public static final double firstBackupStop = 20;
        public static final double encoderBackUp = 95; 
    }

    public class wheelOfFortune{
       // public static final int wheelOfFortunePort = 3; 
    }

}