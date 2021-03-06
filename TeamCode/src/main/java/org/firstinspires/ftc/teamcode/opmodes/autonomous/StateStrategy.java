/*
Copyright (c) 2016 Robert Atkinson

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.hardware.HardwareK9bot;

import static org.firstinspires.ftc.teamcode.opmodes.autonomous.StateStrategy.State.START;
import static org.firstinspires.ftc.teamcode.opmodes.autonomous.StateStrategy.State.STARTED;
import static org.firstinspires.ftc.teamcode.opmodes.autonomous.StateStrategy.State.STOP;
import static org.firstinspires.ftc.teamcode.opmodes.autonomous.StateStrategy.State.STOPPED;
import static org.firstinspires.ftc.teamcode.opmodes.autonomous.StateStrategy.State.TURN;

/**
 * This OpMode uses the common HardwareK9bot class to define the devices on the robot.
 * All device access is managed through the HardwareK9bot class. (See this class for device names)
 * The code is structured as a LinearOpMode
 *
 * This particular OpMode executes a basic Tank Drive Teleop for the K9 bot
 * It raises and lowers the arm using the Gampad Y and A buttons respectively.
 * It also opens and closes the claw slowly using the X and B buttons.
 *
 * Note: the configuration of the servos is such that
 * as the arm servo approaches 0, the arm position moves up (away from the floor).
 * Also, as the claw servo approaches 0, the claw opens up (drops the game element).
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@Autonomous(name="StateStrategy", group="Autonomous")
public class StateStrategy extends LinearOpMode {
    public enum State {
        STOP,
        STOPPED,
        START,
        STARTED,
        TURN,
        PRESS
    }

    public enum Color {
        BLUE,
        RED,
        WHITE
    }

    HardwareK9bot robot = new HardwareK9bot();
    ColorSensor colorSensorBottom;
    ColorSensor colorSensorLeft;
    ColorSensor colorSensorRight;

    private State currState = START;
    private Color currColor = null;

    private ElapsedTime runtime = new ElapsedTime();
    private ElapsedTime stateTimer = new ElapsedTime();

    private int ELAPSED_TIME_STATE_SWITCH = 5;

    private double leftSpeed = 0;
    private double rightSpeed = 0;

    private int END_TIME = 15;

    @Override
    public void runOpMode() {

        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);

        colorSensorBottom = hardwareMap.colorSensor.get("sensor_color");
        //colorSensorLeft = hardwareMap.colorSensor.get("sensor_color");
        //colorSensorRight = hardwareMap.colorSensor.get("sensor_color");
        // Send telemetry message to signify robot waiting;
        telemetry.addData("Say", "Hello Driver");    //
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.startTime();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            telemetry.addData("Red  ", colorSensorBottom.red());
            telemetry.addData("Green", colorSensorBottom.green());
            telemetry.addData("Blue ", colorSensorBottom.blue());
            telemetry.addData("Time: ", runtime.milliseconds());
            telemetry.update();

            if (colorSensorBottom.red() > 200 && colorSensorBottom.green() > 200 && colorSensorBottom.blue() > 200)
            {
                telemetry.addData("Say", "FOUND WHITE LINE");
                telemetry.addData("Time: ", runtime.milliseconds());
                telemetry.update();
                currState = STOP;
            }

            if (currState == STARTED && stateTimer.seconds() > ELAPSED_TIME_STATE_SWITCH)
            {
                telemetry.addData("Say", "STOPPING");
                telemetry.addData("Time: ", runtime.milliseconds());
                telemetry.update();
                currState = STOP;
                stateTimer.reset();
            }

            if (currState == STOPPED && stateTimer.seconds() > ELAPSED_TIME_STATE_SWITCH)
            {
                telemetry.addData("Say", "STARTING");
                telemetry.addData("Time: ", runtime.milliseconds());
                telemetry.update();
                currState = START;
                stateTimer.reset();
            }
            switch(currState) {
                case STOP:
                    stopMotors();
                    break;
                case TURN:
                    turn("left");
                    break;
                case START:
                    startMotors();
                default:
                    // do nothing
            }

            if (runtime.seconds() > END_TIME)
            {
                break;
            }
        }
    }

    public void stopMotors()
    {
        telemetry.addData("Say", "STOP MOTORS");
        telemetry.addData("Time: ", runtime.milliseconds());
        telemetry.update();
        stateTimer.startTime();
        leftSpeed = 0;
        rightSpeed = 0;

        robot.frontLeftMotor.setPower(leftSpeed);
        robot.frontRightMotor.setPower(rightSpeed);
        currState = STOPPED;
    }

    public void startMotors()
    {
        telemetry.addData("Say", "START MOTORS");
        telemetry.addData("Time: ", runtime.milliseconds());
        telemetry.update();
        stateTimer.startTime();
        leftSpeed = .3;
        rightSpeed = .3;

        robot.frontLeftMotor.setPower(leftSpeed);
        robot.frontRightMotor.setPower(rightSpeed);
        currState = STARTED;
    }

    public void turn(String direction)
    {

    }
}
