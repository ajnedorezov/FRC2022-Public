// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.commands.Autonomous.AutoPaths;
import frc.robot.commands.Autonomous.LoadTrajectoryFromPathweaver;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Drivebase;
import frc.robot.subsystems.LED;
// import frc.robot.subsystems.LED;
import frc.robot.subsystems.tracking.LimelightInterface;
import frc.robot.subsystems.tracking.PhotonVisionInterface;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot  {
  private Command m_autonomousCommand;

  private RobotContainer m_robotContainer;
  // private LimelightInterface mLimelightInterface;
  private PhotonVisionInterface mPhotonVisionInterface;


  public static boolean logging = true; // true;
  public static boolean detailedLogging = true;//false; // true;
  public static boolean blueRobot = true; // true

  

  // private LED mLED;

  // try {
  //   File file = new File("home/lvuser/deploy/" + java.time.LocalDateTime.now());
  //   private BufferedWriter logWriter = new BufferedWriter(new FileWriter(file));
  // } catch (IOException exception) {

  // }
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    SmartDashboard.putBoolean("Shooter/Manual RPM Control", false);
    // try {
    //   LoadTrajectoryFromPathweaver t = new LoadTrajectoryFromPathweaver();
    // } catch (IOException e2) {
    //   // TODO Auto-generated catch block
    //   e2.printStackTrace();
    // }
    // Instantiate our RobotContainer.  This will perform all our button bindings, and put our
    // autonomous chooser on the dashboard.
    try {
      m_robotContainer = new RobotContainer();
    } catch (FileNotFoundException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    mPhotonVisionInterface = PhotonVisionInterface.getInstance();
    // mLimelightInterface.setLimeLightLED(1);

  
    //To make sure the auto paths are generated before we try to use them
    try {
      AutoPaths autoPaths = new AutoPaths();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    if (DriverStation.getAlliance().equals(Alliance.Blue)) {
      LED.getInstance().getLED().setLEDs(0, 0, 255);
    } 
    else {
      LED.getInstance().getLED().setLEDs(255, 0, 0);
    }

    // new Thread(
    //   () -> {
    //     UsbCamera camera = CameraServer.startAutomaticCapture();
    //     camera.setResolution(320, 240);
    //     camera.setFPS(5);
        
    //   }
    // ).start();

    // Climber.getInstance().setShoulderPosition(Climber.getInstance().mOffset);

    // LED.getInstance().getLED().setLEDs(255, 0, 0);

    // SmartDashboard.putBoolean("Tower/Beam Break State", false);

    // SmartDashboard.putBoolean("Is Team Color Blue?", true);
    // SmartDashboard.putBoolean("Detailed Logging", false);
  
    }
  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // Runs the Scheduler.  This is responsible for polling buttons, adding newly-scheduled
    // commands, running already-scheduled commands, removing finished or interrupted commands,
    // and running subsystem periodic() methods.  This must be called from the robot's periodic
    // block in order for anything in the Command-based framework to work.
    mPhotonVisionInterface.run();
    if (true) {
      mPhotonVisionInterface.logToDashBoard();
    }
    CommandScheduler.getInstance().run();
    
    // detailedLogging = SmartDashboard.getBoolean("Detailed Logging", detailedLogging);
    // mLED.periodic();
    // blueRobot = SmartDashboard.getBoolean("Blue Robot", blueRobot);

  }

  /** This function is called once each time the robot enters Disabled mode. */
  @Override
  public void disabledInit() {
    Drivebase.getInstance().setAllDriveMode(NeutralMode.Coast);
 //   mLimelightInterface.setLimeLightLED(1);
  }

  @Override
  public void disabledPeriodic() {}

  /** This autonomous runs the autonomous command selected by your {@link RobotContainer} class. */
  @Override
  public void autonomousInit() {
  //  mLimelightInterface.setLimeLightLED(3);
    Drivebase.getInstance().setAllDriveMode(NeutralMode.Brake);
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {
    // Climber.getInstance().setShoulderPosition(5.0);
  //  mLimelightInterface.setLimeLightLED(3);
    Drivebase.getInstance().getThetaController().enableContinuousInput(-Math.PI, Math.PI);
    Drivebase.getInstance().setAllDriveMode(NeutralMode.Brake);
    Drivebase.getInstance().drive(new ChassisSpeeds(0.0, 0.0, 0.0));


    // LED.getInstance().setBottomLEDColor(Color.kRed);
    // LED.getInstance().setTopLEDColor(Color.kBlue);
    // LED.getInstance().setFiringLEDColor(Color.kGreen);

    // LED.getInstance().getLED().setLEDs(0, 255, 0);
    
    
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }

    
    // Using the cancoder, try to figure out what the offset from the ideal starting position is in order to set the falcon encoders properly
    // so our arm positions will stay the same regardless of where the arm starts on powerup
    // double trueSholderPosition = Climber.getShoulderAbsolutePosition() * Constants.Climber.CANCODER_TO_OUTPUT_STAGE + Constants.Climber.SHOULDER_STARTING_POSITION; 
    // Climber.setShoulderEncoderPosition(trueSholderPosition);
    // SmartDashboard.putNumber("Climber/True Shoulder Position", trueSholderPosition);
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {}

  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}
