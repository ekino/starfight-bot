package core;

import java.util.Collection;
import java.util.List;

import com.ekino.animation.devoxx.RestPlayerAlgorithm;
import com.ekino.animation.devoxx.model.World;
import com.ekino.animation.devoxx.model.actions.Action;
import com.ekino.animation.devoxx.model.actions.ActionList;
import com.ekino.animation.devoxx.model.actions.Direction;
import com.ekino.animation.devoxx.model.actions.Move;
import com.ekino.animation.devoxx.model.army.Ship;
import com.ekino.animation.devoxx.model.base.DarkPlanet;
import com.ekino.animation.devoxx.model.base.Location;
import com.ekino.animation.devoxx.model.base.Square;
import com.google.common.collect.Lists;

public class PlayerAlgorithmDefault implements RestPlayerAlgorithm {

	/**
	 * Compléter par votre pseudo
	 * 
	 * @return votre pseudo
	 */
	@Override
	public String ping() {
		return "nico-bot";
	}

	/**
	 * {@inheritDoc RestPlayerAlgorithm}
	 */
	@Override
	public ActionList turn(World world) {
		Collection<Square> elementsAPortee = world.getScanner();
		List<Ship> ennemies = Lists.newArrayList();
		DarkPlanet dongeon = null;
		for (Square aCase : elementsAPortee) {
			if (aCase instanceof Ship) {
				ennemies.add((Ship) aCase);
			}
			if (aCase instanceof DarkPlanet) {
				dongeon = (DarkPlanet) aCase;
			}
		}
		List<Action> actions = Lists.newArrayList();
		for (Ship ennemy : ennemies) {
			Location position = ennemy.getLocation();
			for (Ship soldat : world.getShips()) {
				if (isNear(position, soldat.getLocation(), 4)) {
					actions.add(soldat.attack(position));
					continue;
				}
			}
		}
		int dx, dy;
		if (dongeon == null) {
			dx = world.getWidth() / 2;
			dy = world.getHeight() / 2;
		} else {
			dx = dongeon.getLocation().getX();
			dy = dongeon.getLocation().getY();
		}
		for (Ship soldier : world.getShips()) {
			Location location = soldier.getLocation();
			Move move = moveSoldat(location.getX(), location.getY(), dx, dy);
			if (move != null) {
				List<Move> moves = Lists.newArrayList(move);
				int sx, sy;

				for (int i = 0; i < 5; i++) {
					sx = applyOnX(location.getX(), move);
					sy = applyOnY(location.getY(), move);

					move = moveSoldat(sx, sy, dx, dy);
					if (move == null) {
						break;
					}
					moves.add(move);
				}
				actions.add(soldier.move(moves));
			}
		}

		return ActionList.valueOf(actions);
	}

	private int applyOnY(int y, Move move) {
		if (move.getDirection() == Direction.NORTH) {
			return y - 1;
		} else if (move.getDirection() == Direction.SOUTH) {
			return y + 1;
		}
		return y;

	}

	private int applyOnX(int x, Move move) {
		if (move.getDirection() == Direction.EAST) {
			return x + 1;
		} else if (move.getDirection() == Direction.WEST) {
			return x - 1;
		}
		return x;
	}

	private boolean isNear(Location a, Location b, int range) {
		return isNear(a.getX(), a.getY(), b.getX(), b.getY(), range);
	}

	private boolean isNear(int xa, int ya, int xb, int yb, int range) {
		return Math.abs(xa - xb) < range && Math.abs(ya - yb) < range;
	}

	private Move moveSoldat(int xo, int yo, int xd, int yd) {
		Direction direction = null;
		if (isNear(xo, yo, xd, yd, 2)) {
			return null;
		}
		boolean choice = Math.random() > 0.5;

		if (choice) {
			if (xo - xd > 0) {
				direction = Direction.WEST;
			} else {
				direction = Direction.EAST;
			}
		} else {
			if (yo - yd > 0) {
				direction = Direction.NORTH;
			} else {
				direction = Direction.SOUTH;
			}
		}
		return new Move(direction, 1);
	}
}
