package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.controls.DutyCycleOut;

public class Robot extends TimedRobot {

  private XboxController controller;
  private TalonFX elevatorMotor;
  private final DutyCycleOut dutyOut = new DutyCycleOut(0);

  // Encoder-based software limits (in motor rotations)
  private final double MAX_HEIGHT = 5.0; // adjust based on your elevator
  private final double MIN_HEIGHT = 0.0;

  @Override
  public void robotInit() {
    controller = new XboxController(0);
    elevatorMotor = new TalonFX(1);

    // Zero encoder at startup (assume elevator starts at bottom)
    elevatorMotor.setPosition(0.0);
    System.out.println("Robot initialized, encoder zeroed");
  }

  @Override
  public void teleopPeriodic() {
    boolean yPressed = controller.getYButton();
    boolean aPressed = controller.getAButton();

    // Get Falcon 500 integrated sensor position (in rotations)
    double position = elevatorMotor.getPosition().getValueAsDouble();
    System.out.println("Current Position: " + position);

    // Move up only if below max height
    if (yPressed && position < MAX_HEIGHT) {
      elevatorMotor.setControl(dutyOut.withOutput(0.5));
      System.out.println("Elevator moving up");
    }
    // Move down only if above min height
    else if (aPressed && position > MIN_HEIGHT) {
      elevatorMotor.setControl(dutyOut.withOutput(-0.5));
      System.out.println("Elevator moving down");
    }
    // Otherwise stop
    else {
      elevatorMotor.setControl(dutyOut.withOutput(0.0));
      System.out.println("Elevator stopped");
    }

    System.out.flush();
  }
}
