pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
		maven("https://maven.fabricmc.net/")
	}
}

plugins {
	id("dev.kikugie.stonecutter") version "0.9.8"
}

stonecutter {
	create(rootProject) {
		versions("26.3", "26.4-snapshot-6")
	}
}

rootProject.name = "doumi"
