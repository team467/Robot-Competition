/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.stateMachine;

import frc.robot.RobotMap;
import frc.robot.gamepieces.Indexer;

frc.robot.gamepieces.Indexer;/**
 * Add your docs here.
 */
enum IndexerState implements State {

    Idle {
        public void enter() {
            // Noop
        }

        public State action() {
            return this;
        }

        public void exit() {
            // Noop
        }
    },

    Loading_Off {
        public void enter() {
            // Noop
        }

        public State action() {
            if (!RobotMap.HAS_INDEXER) {
                return Idle; 
            }
            Indexer.IndexBelt.actuate(OFF);
            return this;
        }

        public void exit() {
            // Noop
        }
    },

    Loading_On {
        public void enter() {
            // Noop
        }

        public State action() {

            return this;
        }

        public void exit() {
            // Noop
        }
    },

    Staging_Off {
        public void enter() {
            // Noop
        }

        public State action() {
            return this;
        }

        public void exit() {
            // Noop
        }
    },

    Staging_On {
        public void enter() {
            // Noop
        }

        public State action() {
            return this;
        }

        public void exit() {
            // Noop
        }
    },
    
    Reverse {
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