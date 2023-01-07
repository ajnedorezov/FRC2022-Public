// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.Climber;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.ClimberHooks;

public class SetShortSidePosition extends CommandBase {
  ClimberHooks mClimberHooks;
  double mSetPoint;
  /** Creates a new SetShortSidePosition. */
  public SetShortSidePosition(ClimberHooks climber, double degrees) {
    mClimberHooks = climber;
    mSetPoint = degrees;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(mClimberHooks);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    mClimberHooks.setShortSidePosition(mSetPoint, mClimberHooks.getHookAlpha());
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    // return Math.abs(mClimber.getShoulderPosition() - mSetPoint) < Constants.Climber.POSITION_TOLERANCE;
    return mClimberHooks.shortSideHooksInPosition();
  }
}
