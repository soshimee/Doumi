plugins {
	id("net.fabricmc.fabric-loom") version "1.19-SNAPSHOT"
}

version = "${property("mod_version")}+${sc.current.version}"
base.archivesName = property("mod_id") as String + "-fabric"

val targetJava = when {
	sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
	sc.current.parsed >= "1.20.6" -> JavaVersion.VERSION_21
	sc.current.parsed >= "1.18" -> JavaVersion.VERSION_17
	sc.current.parsed >= "1.17" -> JavaVersion.VERSION_16
	else -> JavaVersion.VERSION_1_8
}

dependencies {
	minecraft("com.mojang:minecraft:${sc.current.version}")

	implementation("net.fabricmc:fabric-loader:${property("loader_version")}")
}

java {
	withSourcesJar()
	targetCompatibility = targetJava
	sourceCompatibility = targetJava
}

sourceSets.main {
	resources.srcDir("resources")
}

tasks.processResources {
	val props = mapOf(
		"mod_id" to project.property("mod_id"),
		"mod_name" to project.property("mod_name"),
		"version" to project.version,
		"loader_dependency" to project.property("loader_dependency"),
		"minecraft_dependency" to project.property("minecraft_dependency")
	)
	inputs.properties(props)
	filesMatching("fabric.mod.json") {
		expand(props)
	}
}
