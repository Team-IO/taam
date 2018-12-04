package net.teamio.taam.piping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by oliver on 2017-12-07.
 */
public class PressureSimulator {

	private static final ThreadLocal<ArrayList<IPipe>> connected = new ThreadLocal<ArrayList<IPipe>>() {
		@Override
		protected ArrayList<IPipe> initialValue() {
			return new ArrayList<IPipe>(6);
		}
	};

	public static final Comparator<IPipe> pipePressureComparator = new Comparator<IPipe>() {
		@Override
		public int compare(IPipe pipe1, IPipe pipe2) {
			int p1 = pipe1.getPressure();
			int p2 = pipe2.getPressure();
			return p1 < p2 ? -1 : (p1 == p2 ? 0 : 1);
		}
	};

	public static void simulate(PipeNetwork network) {
		if (network.needsRescan) {
			network.rescan();
		}

		List<IPipe> connected = PressureSimulator.connected.get();
		for (IPipe pipe : network.getPipes()) {
			connected.clear();
			network.graph.edgesFrom(pipe, connected);

			if (connected.isEmpty()) {
				continue;
			}

			/*
			 * Update Pressure from surrounding pipes
			 */

			// Sort surrounding pipes by pressure
			Collections.sort(connected, pipePressureComparator);

			for (int i = 0; i < connected.size(); i++) {
				//TODO: limit maximum amount of transferred fluid/pressure
				//TODO: what do we do with multiple pipes being pushed to?

				IPipe other = connected.get(i);
				// Only use half the difference to even out the pipes
				// (and not pump everything back in the second possible iteration)
				int pressureDiff = pipe.getPressure() - other.getPressure();
				if (pressureDiff > 0) {
					// Only simulate pushing, nothing "sucks" on this network
					// The reverse is done from the other pipe which will always be part of the same simulation!

					// Only transfer half the content at most - but always transfer something
					if (pressureDiff > 1) {
						pressureDiff = pressureDiff * 3 / 4;
					}
					// More pressure than the other, push content
					int transferred = PipeUtil.transferContent(pipe, other, pressureDiff);
					// If the content was not enough, pass on the raw pressure (unless pipe is neutral)
					if (!pipe.isNeutral()) {
						if (other.isNeutral()) {
							if (transferred > 0) {
								// We have transferred something, relive pressure.
								// Otherwise do nothing, we assume the neutral pipe is full
								pipe.applyPressure(-pressureDiff + transferred);
							}
						} else if (transferred < pressureDiff) {
							pressureDiff -= transferred;
							pipe.applyPressure(-pressureDiff);
							other.applyPressure(pressureDiff);
						}
					}
				}
			}

			int sign = Integer.signum(pipe.getPressure());
			if (sign != 0) {
				// Slowly but surely lose the pressure in the pipe
				pipe.applyPressure(-sign);
			}
		}
	}
}
