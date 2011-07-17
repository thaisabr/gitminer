/*
 * Copyright 2011 IBM Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.wagstrom.research.github;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.api.v2.schema.Repository;
import com.github.api.v2.schema.User;
import com.github.api.v2.services.RepositoryService;

public class RepositoryMiner {
	private RepositoryService service = null;
	private Logger log;
	
	public RepositoryMiner(RepositoryService service) {
		this.service = service;
		log = LoggerFactory.getLogger(this.getClass());
	}
	
	public Repository getRepositoryInformation(String username, String reponame) {
		Repository repo = service.getRepository(username, reponame);
		log.debug("Fetched repository: " + username + "/" + reponame);
		return repo;
	}
	
	public List<String> getRepositoryCollaborators(String username, String reponame) {
		List<String> collabs = service.getCollaborators(username, reponame);
		log.debug("Fetched collaborators: " + username + "/" + reponame + " number: " + collabs.size());
		return collabs;
	}
	
	public List<User> getRepositoryContributors(String username, String reponame) {
		List<User> contributors = service.getContributors(username, reponame);
		log.debug("Fetched contributors: " + username + "/" + reponame + " number: " + contributors.size());
		return contributors;
	}
	
	public List<Repository> getUserRepositories(String username) {
		List<Repository> repos = service.getRepositories(username);
		Map<String, String> headers = service.getRequestHeaders();
		log.debug("Fetched repositories for user: " + username + " number: " + repos.size());
		return repos;
	}
	

}
