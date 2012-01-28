/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2010
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>, et al.
 *
 * Infinitest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Infinitest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Infinitest.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.infinitest.eclipse.event;

import static com.google.common.collect.Lists.*;
import static java.util.Arrays.*;

import java.util.*;

import org.eclipse.core.resources.*;
import org.infinitest.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

@Component
class CoreUpdateNotifier implements IResourceChangeListener {
	private final List<EclipseEventProcessor> processors;
	private final EventQueue queue;

	@Autowired
	CoreUpdateNotifier(EventQueue queue) {
		this.queue = queue;
		processors = newArrayList();
	}

	@Autowired
	public void addProcessor(EclipseEventProcessor... eventProcessors) {
		processors.addAll(asList(eventProcessors));
	}

	/**
	 * http://www.eclipse.org/articles/Article-Resource-deltas/resource-deltas.
	 * html
	 */
	public void resourceChanged(final IResourceChangeEvent event) {
		if (event.getDelta() != null) {
			processEvent(event);
		}
	}

	public void processEvent(IResourceChangeEvent event) {
		for (EclipseEventProcessor processor : processors) {
			if (processor.canProcessEvent(event)) {
				queue.pushNamed(new EventProcessorRunnable(processor, event));
			}
		}
	}
}
