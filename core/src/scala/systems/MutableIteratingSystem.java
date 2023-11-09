package systems;

/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;

/**
 * A simple EntitySystem that iterates over each entity and calls processEntity() for each entity every time the EntitySystem is
 * updated. This is really just a convenience class as most systems iterate over a list of entities.
 * @author Stefan Bachmann
 */
public abstract class MutableIteratingSystem extends EntitySystem {
    protected Family family;
    protected ImmutableArray<Entity> entities;
    protected Engine engine;

    /**
     * Instantiates a system that will iterate over the entities described by the Family.
     */
    public MutableIteratingSystem () {
        
    }
    /**
     * Instantiates a system that will iterate over the entities described by the Family.
     * @param family The family of entities iterated over in this System
     */
    public MutableIteratingSystem (Family family) {
        this(family, 0);
    }

    /**
     * Instantiates a system that will iterate over the entities described by the Family, with a specific priority.
     * @param family The family of entities iterated over in this System
     * @param priority The priority to execute this system with (lower means higher priority)
     */
    public MutableIteratingSystem (Family family, int priority) {
        super(priority);

        this.family = family;
    }

    @Override
    public void addedToEngine (Engine engine) {
        this.engine=engine;
    }

    @Override
    public void removedFromEngine (Engine engine) {
        entities = null;
    }

    @Override
    public void update (float deltaTime) {
        startProcessing();
//        Gdx.app.log("MutableIteratingSystem","aha update");
        for (int i = 0; i < entities.size(); ++i) {
//            Gdx.app.log("MutableIteratingSystem",""+entities.size());
            processEntity(entities.get(i), deltaTime);
        }
        endProcessing();
    }

    /**
     * @return set of entities processed by the system
     */
    public ImmutableArray<Entity> getEntities () {
        return entities;
    }

    /**
     * @return the Family used when the system was created
     */
    public Family getFamily () {
        return family;
    }

    /**
     * This method is called on every entity on every update call of the EntitySystem. Override this to implement your system's
     * specific processing.
     * @param entity The current Entity being processed
     * @param deltaTime The delta time between the last and current frame
     */
    protected abstract void processEntity (Entity entity, float deltaTime);

    /**
     * This method is called once on every update call of the EntitySystem, before entity processing begins. Override this method to
     * implement your specific startup conditions.
     */
    public void startProcessing() {
        entities = engine.getEntitiesFor(family);
    }

    /**
     * This method is called once on every update call of the EntitySystem after entity processing is complete. Override this method to
     * implement your specific end conditions.
     */
    public void endProcessing() {}
}