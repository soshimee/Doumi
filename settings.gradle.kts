pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
		maven("https://maven.fabricmc.net/")
		maven("https://maven.neoforged.net/releases/")
	}
}

plugins {
	id("dev.kikugie.stonecutter") version "0.9.8"
}

stonecutter {
	create(rootProject) {
		fun match(version: String, vararg loaders: String) {
			for (loader in loaders) {
				version("$version-$loader", version).buildscript("build.$loader.gradle.kts")
			}
		}

		match("26.3", "fabric", "neoforge")
		match("26.4-snapshot-6", "fabric")

		vcsVersion = "26.3-fabric"
	}
}

rootProject.name = "doumi"
