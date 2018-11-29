package com.github.skjolberg.packing;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

import com.github.skjolberg.packing.impl.Adapter;
import com.github.skjolberg.packing.impl.LAFFResult;
import com.github.skjolberg.packing.impl.PackResult;

public class ThreeDimensionalPackager extends Packager {

	public ThreeDimensionalPackager(List<Container> containers) {
		super(containers);
	}

	public ThreeDimensionalPackager(List<Container> containers, boolean rotate3d, boolean binarySearch) {
		super(containers, rotate3d, binarySearch);
	}

	@Override
	protected Adapter adapter() {
		return new Adapter() {

			private List<Box> boxes;
			private LAFFResult previous;
			private List<Container> containers;

			@Override
			public PackResult attempt(int index, BooleanSupplier interrupt) {
				LAFFResult result = ThreeDimensionalPackager.this.pack(new ArrayList<>(boxes), containers.get(index), interrupt);

				return previous = result;
			}

			@Override
			public void initialize(List<BoxItem> boxItems, List<Container> container) {
				this.containers = container;

				List<Box> boxClones = new ArrayList<>(boxItems.size() * 2);

				for(BoxItem item : boxItems) {
					Box box = item.getBox();
					boxClones.add(box);
					for(int i = 1; i < item.getCount(); i++) {
						boxClones.add(box.clone());
					}
				}

				this.boxes = boxClones;
			}

			@Override
			public Container accepted(PackResult result) {
				LAFFResult laffResult = (LAFFResult)result;

				this.boxes = laffResult.getRemainingBoxes();

				if(previous == result) {
					return laffResult.getContainer();
				}

				// calculate again
				Container container = laffResult.getContainer();
				List<Box> boxes = new ArrayList<>(this.boxes.size());
				for(Level level : container.getLevels()) {
					for(Placement placement : level) {
						boxes.add(placement.getBox());
					}
				}

				container.clear();

				LAFFResult pack = ThreeDimensionalPackager.this.pack(boxes, container, Long.MAX_VALUE);

				return pack.getContainer();
			}

			@Override
			public boolean hasMore(PackResult result) {
				LAFFResult laffResult = (LAFFResult)result;
				return !laffResult.getRemainingBoxes().isEmpty();
			}

		};

	}

	protected LAFFResult pack(ArrayList<Box> boxes, Container container, BooleanSupplier interrupt) {
		return null;
	}

	protected LAFFResult pack(List<Box> boxes, Container container, long maxValue) {
		return null;
	}
}
