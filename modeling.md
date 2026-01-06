We created the corresponding FSM from the Assignment sheet and ported it to UPPAAL.

This was done in the "CAS" template. Afterwards, for testing, we had to add a "USER" template which provided the CAS with random (nondeterministic) inputs.

We had to model the `SilentAndOpen` multiple times as else it would have not been possible to correctly model the `Car` and `Alarm` in two different templates.

## Verifier
We created some Temporal Logical Constraints to verify that the model is indeed correct. 