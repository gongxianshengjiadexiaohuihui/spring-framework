/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.servlet.resource;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;


/**
 * A simple {@code ResourceResolver} that tries to find a resource under the given
 * locations matching to the request path.
 *
 * <p>This resolver does not delegate to the {@code ResourceResolverChain} and is
 * expected to be configured at the end in a chain of resolvers.
 *
 * @author Jeremy Grelle
 * @author Rossen Stoyanchev
 * @since 4.1
 */
public class PathResourceResolver implements ResourceResolver {

	private static final Log logger = LogFactory.getLog(PathResourceResolver.class);


	@Override
	public Resource resolveResource(HttpServletRequest request,
			String requestPath, List<Resource> locations, ResourceResolverChain chain) {

		return getResource(requestPath, locations);
	}

	@Override
	public String getPublicUrlPath(String resourceUrlPath, List<Resource> locations, ResourceResolverChain chain) {
		return (getResource(resourceUrlPath, locations) != null) ? resourceUrlPath : null;
	}

	private Resource getResource(String path, List<Resource> locations) {
		for (Resource location : locations) {
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("Looking for \"" + path + "\" under " + location);
				}
				Resource resource = location.createRelative(path);
				if (resource.exists() && resource.isReadable()) {
					if (logger.isDebugEnabled()) {
						logger.debug("Resource exists and is readable");
					}
					return resource;
				}
				else if (logger.isTraceEnabled()) {
					logger.trace("Relative resource doesn't exist or isn't readable: " + resource);
				}
			}
			catch (IOException ex) {
				logger.debug("Failed to create relative resource - trying next resource location", ex);
			}
		}
		return null;
	}

}
