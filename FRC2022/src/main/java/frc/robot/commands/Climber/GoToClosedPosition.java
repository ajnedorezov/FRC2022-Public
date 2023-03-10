// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.Climber;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.ClimberHooks;

public class GoToClosedPosition extends CommandBase {

  ClimberHooks mHooks;
  BooleanSupplier mLongSide;
  BooleanSupplier mShortSide;

  /** Creates a new GoToClosedPosition. */
  public GoToClosedPosition(ClimberHooks hooks, BooleanSupplier longSide, BooleanSupplier shortSide) {
    mHooks = hooks;
    mLongSide = longSide;
    mShortSide = shortSide;
    // Use addRequirements() here to declare subsystem dependencies.
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if (mLongSide.getAsBoolean()) {
      mHooks.setLongSidePositionClosed(mHooks.getHookAlpha());
    }
    if (mShortSide.getAsBoolean()) {
      mHooks.setShortSidePositionClosed(mHooks.getHookAlpha());
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
