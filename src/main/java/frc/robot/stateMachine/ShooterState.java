package frc.robot.stateMachine;;

enum ShooterState implements State {
    Idle {
        public void enter() {
            // Noop
        }

        public State action() {
            if (System.currentTimeMillis() % 2000 > 1000) {
                return Prepping;
            }
            System.out.print("I");
            return this;
        }

        public void exit() {
            // Noop
        }
    },

    Prepping {
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
