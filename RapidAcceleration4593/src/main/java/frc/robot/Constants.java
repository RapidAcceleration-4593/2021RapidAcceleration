package frc.robot;

public class Constants {
    public class driveTrain {
        public static final int FRMPort = 11;
        public static final int RRMPort = 10;
        public static final int FLMPort = 9;
        public static final int RLMPort = 8;
    }

    public class shooter {
        public static final int shooterLeftPort = 12;
        public static final int shooterRightPort = 13;

        public static final int turretPort = 1;

        public static final double shooterFF = .00001;

        public static final double threshold = .5;

        public static final double turnSpeed = .8;

    }

    public class intake {
        public static final int intakeMotorPort = 6;
        public static final int hopperMotorPort = 9;
        public static final int intoShooterMotorPort = 10;
    }

    public class climber {
        public static final int climberMotor1Port = 7;
    }

    public class controllers {
        public static final int controllerOnePort = 0;
    }

}