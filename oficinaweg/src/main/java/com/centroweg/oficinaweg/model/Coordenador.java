package com.centroweg.oficinaweg.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("COORDENADOR")
public class Coordenador extends Professor {
}

