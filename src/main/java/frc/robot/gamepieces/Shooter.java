package frc.robot.gamepieces;

import frc.robot.gamepieces.ShooterController;
import frc.robot.RobotMap;


enum ShooterState implements State {

    Idle {
        public void enter() {
            // Noop
        }

        public State action() {
            if (System.currentTimeMillis() % 2000 > 1000) {
                return LoadingBall;
            }
            System.out.print("I");
            return this;
        }

        public void exit() {
            // Noop
        }
    },

    LoadingBall {
        public void enter() {
            // float distance = Sensor.getDistance();
            // int desiredRPM = (int)(distance * 1 * 1);
        }

        public State action() {
            if (System.currentTimeMillis() % 2000 < 1000) {
                return Idle;
            }

            System.out.print("P");
            return this;
        }

        public void exit() {
            // Noop
        }
    },

    AdjustingSpeed {

        private ShooterController shooter;

        public void enter() {

        }

        public State action() {
            if (shooter.atSpeed()){
                if (true) {

                }
            }
            return this;
        }

        public void exit() {
            // Noop
        }
    },

    Shooting {
        public void enter() {
            // Noop
        }

        public State action() {
            return this;
        }

        public void exit() {
            // Noop
        }
    }
}
