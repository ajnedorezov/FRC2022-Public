// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.Autonomous;

import java.util.function.Consumer;
import java.util.function.Supplier;
import static edu.wpi.first.wpilibj.util.ErrorMessages.requireNonNullParam;

import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Subsystem;
import frc.robot.utilities.DaisyMath;

/** Add your docs here. */
public class SwerveControllerClass extends CommandBase {

    private final Timer m_timer = new Timer();
    private final Trajectory m_trajectory;
    private final Supplier<Pose2d> m_pose;
    private final SwerveDriveKinematics m_kinematics;
    private final HolonomicDriveController m_controller;
    private final Consumer<SwerveModuleState[]> m_outputModuleStates;
    private final Supplier<Rotation2d> m_desiredRotation;
    private double timeToFinish;
    private double initialHeading;

    /**
     * Constructs a new SwerveControllerCommand that when executed will follow the
     * provided
     * trajectory. This command will not return output voltages but rather raw
     * module states from the
     * position controllers which need to be put into a velocity PID.
     *
     * <p>
     * Note: The controllers will *not* set the outputVolts to zero upon completion
     * of the path-
     * this is left to the user, since it is not appropriate for paths with
     * nonstationary endstates.
     *
     * @param trajectory         The trajectory to follow.
     * @param pose               A function that supplies the robot pose - use one
     *                           of the odometry classes to
     *                           provide this.
     * @param kinematics         The kinematics for the robot drivetrain.
     * @param xController        The Trajectory Tracker PID controller for the
     *                           robot's x position.
     * @param yController        The Trajectory Tracker PID controller for the
     *                           robot's y position.
     * @param thetaController    The Trajectory Tracker PID controller for angle for
     *                           the robot.
     * @param desiredRotation    The angle that the drivetrain should be facing.
     *                           This is sampled at each
     *                           time step.
     * @param outputModuleStates The raw output module states from the position
     *                           controllers.
     * @param requirements       The subsystems to require.
     */
    @SuppressWarnings("ParameterName")
    public SwerveControllerClass(
            Trajectory trajectory,
            Supplier<Pose2d> pose,
            SwerveDriveKinematics kinematics,
            PIDController xController,
            PIDController yController,
            ProfiledPIDController thetaController,
            Supplier<Rotation2d> desiredRotation,
            Consumer<SwerveModuleState[]> outputModuleStates,
            Subsystem... requirements) {
        m_trajectory = requireNonNullParam(trajectory, "trajectory", "SwerveControllerCommand");
        m_pose = requireNonNullParam(pose, "pose", "SwerveControllerCommand");
        m_kinematics = requireNonNullParam(kinematics, "kinematics", "SwerveControllerCommand");

        m_controller = new HolonomicDriveController(
                requireNonNullParam(xController, "xController", "SwerveControllerCommand"),
                requireNonNullParam(yController, "xController", "SwerveControllerCommand"),
                requireNonNullParam(thetaController, "thetaController", "SwerveControllerCommand"));

        m_outputModuleStates = requireNonNullParam(outputModuleStates, "frontLeftOutput", "SwerveControllerCommand");

        m_desiredRotation = requireNonNullParam(desiredRotation, "desiredRotation", "SwerveControllerCommand");

        addRequirements(requirements);
    }

    /**
     * Constructs a new SwerveControllerCommand that when executed will follow the
     * provided
     * trajectory. This command will not return output voltages but rather raw
     * module states from the
     * position controllers which need to be put into a velocity PID.
     *
     * <p>
     * Note: The controllers will *not* set the outputVolts to zero upon completion
     * of the path-
     * this is left to the user, since it is not appropriate for paths with
     * nonstationary endstates.
     *
     * <p>
     * Note 2: The final rotation of the robot will be set to the rotation of the
     * final pose in the
     * trajectory. The robot will not follow the rotations from the poses at each
     * timestep. If
     * alternate rotation behavior is desired, the other constructor with a supplier
     * for rotation
     * should be used.
     *
     * @param trajectory         The trajectory to follow.
     * @param pose               A function that supplies the robot pose - use one
     *                           of the odometry classes to
     *                           provide this.
     * @param kinematics         The kinematics for the robot drivetrain.
     * @param xController        The Trajectory Tracker PID controller for the
     *                           robot's x position.
     * @param yController        The Trajectory Tracker PID controller for the
     *                           robot's y position.
     * @param thetaController    The Trajectory Tracker PID controller for angle for
     *                           the robot.
     * @param outputModuleStates The raw output module states from the position
     *                           controllers.
     * @param requirements       The subsystems to require.
     */
    @SuppressWarnings("ParameterName")
    public SwerveControllerClass(
            Trajectory trajectory,
            Supplier<Pose2d> pose,
            SwerveDriveKinematics kinematics,
            PIDController xController,
            PIDController yController,
            ProfiledPIDController thetaController,
            Consumer<SwerveModuleState[]> outputModuleStates,
            Subsystem... requirements) {
        this(
                trajectory,
                pose,
                kinematics,
                xController,
                yController,
                thetaController,
                () -> trajectory.getStates().get(trajectory.getStates().size() - 1).poseMeters.getRotation(),
                outputModuleStates,
                requirements);
    }

    @Override
    public void initialize() {
        m_timer.reset();
        m_timer.start();

        initialHeading = m_pose.get().getRotation().getDegrees();

        timeToFinish = m_trajectory.getTotalTimeSeconds() * 0.25;
    }

    @Override
    @SuppressWarnings("LocalVariableName")
    public void execute() {
        double curTime = m_timer.get();
        var desiredState = m_trajectory.sample(curTime);
        Rotation2d heading;
        if (curTime < timeToFinish) {
            heading = Rotation2d.fromDegrees(DaisyMath.boundAngleNeg180to180Degrees(curTime * (m_desiredRotation.get().getDegrees() - initialHeading) / timeToFinish + initialHeading));
        } else {
            heading = m_desiredRotation.get();
        }

        var targetChassisSpeeds = m_controller.calculate(m_pose.get(), desiredState,
                // THE BELOW LINE is the only one we changed
                heading);
        var targetModuleStates = m_kinematics.toSwerveModuleStates(targetChassisSpeeds);

        m_outputModuleStates.accept(targetModuleStates);
    }

    @Override
    public void end(boolean interrupted) {
        m_timer.stop();
    }

    @Override
    public boolean isFinished() {
        return m_timer.hasElapsed(m_trajectory.getTotalTimeSeconds());
    }

}
