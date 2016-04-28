package uk.co.bedroomcoders.pp;

public class PID {

    double epsilon = 0.01f;
    double MAX = 8f;			//For Current Saturation
    double MIN = -8f;
    Double Kp = 8.;          // P
    Double Ki = 0.001;       // I
    Double Kd = 4.;          // D
    double pre_error;
    double integral;
    
    // default constructor makes one with values convienient for player ship rotation...
    public PID() {
        // epsilon, max, min, P, D, I
        this(0.01f, 6f, -6f,
                1., .001, .01f);
    }
    
    public PID(double E,double max, double min, double P, double D, double I) {
        epsilon = E;
        MAX = max;
        MIN = min;
        Kp = P;
        Kd = D;
        Ki = I;
    }

    public double value(double setpoint, double actual_position, double dt) {

        double error;
        double derivative;
        double output;

        //Caculate P,I,D
        error = setpoint - actual_position;

        //In case of error too small then stop intergration
        if (Math.abs(error) > epsilon) {
            integral = integral + error * dt;
        }
        derivative = (error - pre_error) / dt;
        output = Kp * error + Ki * integral + Kd * derivative;

        //Saturation Filter
        if (output > MAX) {
            output = MAX;
        } else if (output < MIN) {
            output = MIN;
        }
        //Update error
        pre_error = error;

        return output;
    }

}
