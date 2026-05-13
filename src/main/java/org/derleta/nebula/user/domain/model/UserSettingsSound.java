package org.derleta.nebula.user.domain.model;

public record UserSettingsSound(long userId, boolean muted, boolean battleCry,
                                int volumeMaster, int volumeMusic, int volumeEffects, int volumeVoices) {
}
