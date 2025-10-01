package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.PositionDutyCycle;

public class Robot extends TimedRobot {

    private XboxController controller;
    private TalonFX elevatorMotor;
    private TalonFX elevatorMotor2;

    private final DutyCycleOut dutyOut = new DutyCycleOut(0);
    private final PositionDutyCycle positionControl = new PositionDutyCycle(0);

    // Preset heights in revolutions (calibrated)
    private final double HEIGHT_ONE = -3.85;   // X button
    private final double HEIGHT_TWO = -13.85;  // B button

    private double targetPos = 0.0; // Last commanded target

    @Override
    public void robotInit() {
        controller = new XboxController(0);
        elevatorMotor = new TalonFX(9);
        elevatorMotor2 = new TalonFX(10);
        System.out.println("Robot initialized");

        var currentconfiguration = new CurrentLimitsConfigs();
        currentconfiguration.SupplyCurrentLimit = 40;
        currentconfiguration.SupplyCurrentLimitEnable = true;
        elevatorMotor.getConfigurator().apply(currentconfiguration);
        elevatorMotor2.getConfigurator().apply(currentconfiguration);

        // Initialize targetPos to current position to avoid jump
        targetPos = elevatorMotor.getPosition().getValueAsDouble();
    }

    @Override
    public void teleopPeriodic() {
        double rotations = elevatorMotor.getPosition().getValueAsDouble();

        // Single-press preset heights
        if (controller.getXButtonPressed()) {
            targetPos = HEIGHT_ONE;
            System.out.println("X pressed: moving to HEIGHT_ONE = " + HEIGHT_ONE);
        }
        if (controller.getBButtonPressed()) {
            targetPos = HEIGHT_TWO;
            System.out.println("B pressed: moving to HEIGHT_TWO = " + HEIGHT_TWO);
        }

        // Manual override (A/Y) — updates targetPos to hold after release
        if (controller.getAButton() && rotations <= -1.3) {
            elevatorMotor.setControl(dutyOut.withOutput(0.1));
            elevatorMotor2.setControl(dutyOut.withOutput(0.1));
            targetPos = rotations;
            System.out.println("Manual up");
        } else if (controller.getYButton() && rotations >= -18) {
            elevatorMotor.setControl(dutyOut.withOutput(-0.1));
            elevatorMotor2.setControl(dutyOut.withOutput(-0.1));
            targetPos = rotations;
            System.out.println("Manual down");
        } else {
            // Hold last target position (preset or manual stop)
            elevatorMotor.setControl(positionControl.withPosition(targetPos));
            elevatorMotor2.setControl(positionControl.withPosition(targetPos));
            System.out.println("Holding/moving to targetPos = " + targetPos);
        }

        System.out.println("Current rotations = " + rotations);
        System.out.flush();
    }
}
