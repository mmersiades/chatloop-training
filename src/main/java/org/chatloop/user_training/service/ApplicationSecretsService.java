package org.chatloop.user_training.service;

import java.util.Optional;

/**
 * Provides access to some secret values which are needed by this application to work properly.
 */
public interface ApplicationSecretsService {
    /**
     * Returns a JWT which should be used when executing requests against the REST Api
     * of the Monolith.
     * @return String
     */
    String monolithRestServiceJWT();
}
