/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.gamepieces;

import frc.robot.RobotMap;
import frc.robot.gamepieces.IndexerAL;

enum Indexer implements State {
   
    
    Idle {
        public void enter() {
            // Noop
        }

        public State action() {
        //     if (AutoMode) {
        //     return Forward;
        // }
            if () {
                
            }
            return this;
        }

        public void exit() {
            // Noop
        }
    },

    Forward {
        public void enter() {
            // Noop
        }

        public State action() {
            IndexerController.callForward();
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
    },

    Manual {
        public void enter() {
            // Noop
        }

        public State action() {
            IndexerAL.callForward();
            return this;
        }

        public void exit() {
            // Noop
        }
    },

}