package io.github.fanoupro.ruche.exemples.conventions_alternatives.reservation.domaine;

import java.util.List;
import java.util.UUID;

public record Reservation(UUID id, List<String> trains) {
}
