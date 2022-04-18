package config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

@Sources("classpath:config.properties")
public interface ServerConfig extends Config {

	@Key("otusUrl")
	String otusUrl();

	@Key("email")
	String email();

	@Key("password")
	String password();

	@Key("phone1")
	String phone1();

	@Key("vk")
	String vk();

	@Key("tg")
	String tg();

	@Key("name")
	String name();

	@Key("surname")
	String surname();

	@Key("birth")
	String birth();
}
