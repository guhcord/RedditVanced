import com.github.redditvanced.gradle.ProjectType

plugins {
	id("com.android.library")
	id("kotlin-android")
	id("maven-publish")
	id("redditvanced")
}

group = "com.github.redditvanced"
version = "1.0.0"

android {
	namespace = "com.github.redditvanced.common"

	compileSdk = 30

	defaultConfig {
		minSdk = 24
		targetSdk = 30
	}

	buildTypes {
		release {
			isMinifyEnabled = false
		}
	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}

	kotlinOptions {
		jvmTarget = "11"
		freeCompilerArgs = freeCompilerArgs +
			"-Xno-call-assertions" +
			"-Xno-param-assertions" +
			"-Xno-receiver-assertions"
	}
}

redditVanced {
	projectType.set(ProjectType.REGULAR)
}

dependencies {
	implementation("com.beust:klaxon:5.5")

	val redditVersion: String by project
	redditApk("::$redditVersion")
}

afterEvaluate {
	publishing {
		publications {
			register(project.name, MavenPublication::class) {
				from(components["release"])
				artifact(tasks["releaseSourcesJar"])
			}
		}

		repositories {
			val username = System.getenv("MAVEN_USERNAME")
			val password = System.getenv("MAVEN_PASSWORD")

			if (username == null || password == null)
				mavenLocal()
			else maven {
				credentials {
					this.username = username
					this.password = password
				}
				setUrl("https://redditvanced.ddns.net/maven/releases")
			}
		}
	}
}
