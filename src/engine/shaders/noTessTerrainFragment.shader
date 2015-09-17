#version 410 core

in vec3 surfaceNormal;
in vec2 pass_textureCoordinates;
in vec3 toLightVector[4];
in vec3 toCameraVector;
in float visibility;
in float distance;

out vec4 out_Color;

uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;

uniform vec3 lightColor[4];
uniform vec3 attenuation[4];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;

void main(void){
	
	vec4 blendMapColor = texture(blendMap, pass_textureCoordinates);
	
	float backTextureAmount = 1 - (blendMapColor.r + blendMapColor.g + blendMapColor.b);
	vec2 tiledCoords = pass_textureCoordinates * 40f;
	vec4 backgroundTextureColor = texture(backgroundTexture, tiledCoords) * backTextureAmount;
	vec4 rTextureColor = texture(rTexture,tiledCoords) * blendMapColor.r;
	vec4 gTextureColor = texture(gTexture,tiledCoords) * blendMapColor.g;
	vec4 bTextureColor = texture(bTexture,tiledCoords) * blendMapColor.b;
	
	vec4 totalColor = backgroundTextureColor + rTextureColor + gTextureColor + bTextureColor;
	
	vec3 unitNormal = normalize(surfaceNormal);
	vec3 unitVectorToCamera = normalize(toCameraVector);
	
	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);
	for(int i = 0; i < 4; i++){
		vec3 unitLightVector = normalize(toLightVector[i]);
		float attFactor = attenuation[i].x + (attenuation[i].y * distance) +(attenuation[i].z * (distance * distance));
		float nDotl = dot(unitNormal, unitLightVector);
		float brightness = max(nDotl, 0.2);
		vec3 lightDirection = -unitLightVector;
		vec3 reflectedLightDirection = reflect(lightDirection,unitNormal);
		float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
		specularFactor = max(specularFactor,0.0f);
		float dampedFactor = pow(specularFactor, shineDamper);
		totalDiffuse = totalDiffuse + (brightness * lightColor[i])/attFactor;
		totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColor[i])/attFactor;
	}
	totalDiffuse = max(totalDiffuse, 0.2);
	out_Color = vec4(totalDiffuse, 1.0f) * totalColor + vec4(totalSpecular, 1);
	out_Color = mix(vec4(skyColor,1.0), out_Color, visibility);
}

/*
void main(void){
	
	vec4 blendMapColor = texture(blendMap, texCoord_FS_in);
	
	float backTextureAmount = 1 - (blendMapColor.r + blendMapColor.g + blendMapColor.b);
	vec2 tiledCoords = texCoord_FS_in * 40f;
	vec4 backgroundTextureColor = texture(backgroundTexture, tiledCoords) * backTextureAmount;
	vec4 rTextureColor = texture(rTexture,tiledCoords) * blendMapColor.r;
	vec4 gTextureColor = texture(gTexture,tiledCoords) * blendMapColor.g;
	vec4 bTextureColor = texture(bTexture,tiledCoords) * blendMapColor.b;
	
	vec4 totalColor = backgroundTextureColor + rTextureColor + gTextureColor + bTextureColor;
	
	vec3 unitNormal = normalize(normal_FS_in);
	vec3 unitVectorToCamera = normalize((cameraPos - worldPos_FS_in));
	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);
	vec3 toLightVector[4];
	vec4 positionRelativeToCam = viewMatrix *  vec4(worldPos_FS_in, 1);
	float distance = length(positionRelativeToCam.xyz);
	float visibility = exp(-pow((distance * density),gradient));
	for(int i = 0; i < 4; i++){
		toLightVector[i] = lightPosition[i] - worldPos_FS_in;
		float distance = length(toLightVector[i]);
		float attFactor = attenuation[i].x + (attenuation[i].y * distance) +(attenuation[i].z * (distance * distance));
		vec3 unitLightVector = normalize(toLightVector[i]);
		float nDotl = dot(unitNormal, unitLightVector);
		float brightness = max(nDotl, 0.2);
		vec3 lightDirection = -unitLightVector;
		vec3 reflectedLightDirection = reflect(lightDirection,unitNormal);
		float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
		specularFactor = max(specularFactor,0.0f);
		float dampedFactor = pow(specularFactor, shineDamper);
		totalDiffuse = totalDiffuse + (brightness * lightColor[i])/attFactor;
		totalSpecular = totalSpecular +  (dampedFactor * reflectivity * lightColor[i])/attFactor;
	}
	totalDiffuse = max(totalDiffuse, 0.2);
	out_Color = vec4(totalDiffuse, 1.0f) * totalColor + vec4(totalSpecular, 1);
	out_Color = mix(vec4(skyColor,1.0), out_Color, visibility);
}*/