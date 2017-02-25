package org.tlsfree.core.persistence;

import java.io.Serializable;
import java.util.Properties;

import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.rholder.fauxflake.IdGenerators;
import com.github.rholder.fauxflake.api.IdGenerator;
import com.github.rholder.fauxflake.api.MachineIdProvider;

public class FlakeIdGenerator implements IdentifierGenerator, Configurable {
	private static final Logger log = LoggerFactory.getLogger(FlakeIdGenerator.class);
	private IdGenerator idGenerator;
	
	@Override
	public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
		final String nodeId = params.getProperty("id.nodeid");
		
		if (!NumberUtils.isCreatable(nodeId)) {
			idGenerator = IdGenerators.newSnowflakeIdGenerator();
		} else {
			idGenerator = IdGenerators.newSnowflakeIdGenerator(new MachineIdProvider() {
				@Override
				public long getMachineId() {
					return Long.valueOf(nodeId);
				}
			});
		}
	}

	@Override
	public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
		try {
			return idGenerator.generateId(1000).asLong();
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
			throw new HibernateException(e);
		}
	}
}