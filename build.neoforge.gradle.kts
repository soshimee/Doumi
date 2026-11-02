plugins {
	id("dev.kikugie.stonecutter")
	id("net.neoforged.moddev") version "2.0.148"
	id("maven-publish")
}

version = "${property("mod_version")}+${sc.current.version}"
base.archivesName = property("mod_id") as String + "-neoforge"

val targetJava = when {
	sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
	sc.current.parsed >= "1.20.6" -> JavaVersion.VERSION_21
	sc.current.parsed >= "1.18" -> JavaVersion.VERSION_17
	sc.current.parsed >= "1.17" -> JavaVersion.VERSION_16
	else -> JavaVersion.VERSION_1_8
}

java {
	withSourcesJar()
	targetCompatibility = targetJava
	sourceCompatibility = targetJava
}

neoForge {
	version = property("neoforge_version") as String

	runs {
		register("client") {
			client()
		}
	}

	mods {
		register(property("mod_id") as String) {
			sourceSet(sourceSets.main.get())
		}
	}
}

sourceSets.main {
	resources.srcDir("resources")
}

tasks.processResources {
	val props = mapOf(
		"mod_id" to project.property("mod_id"),
		"mod_name" to project.property("mod_name"),
		"version" to project.version,
		"neoforge_dependency" to project.property("neoforge_dependency"),
		"minecraft_dependency" to project.property("minecraft_dependency")
	)
	inputs.properties(props)
	filesMatching("META-INF/neoforge.mods.toml") {
		expand(props)
	}
}
