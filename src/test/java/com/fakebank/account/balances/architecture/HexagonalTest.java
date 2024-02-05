package com.fakebank.account.balances.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;

@AnalyzeClasses(packages = "com.fakebank.account.balances")
public class HexagonalTest {
    public static final String ENTITIES = "Entities";
    public static final String INTERACTORS = "Interactors";
    public static final String REPOSITORIES = "Repositories";
    public static final String DATASOURCES = "Datasources";
    public static final String TRANSPORT_LAYERS = "TransportLayers";
    public static final String CONFIGS = "Configs";
    @ArchTest
    public static final ArchRule layersValidator = Architectures.layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer(ENTITIES).definedBy("..entities..")
            .layer(INTERACTORS).definedBy("..interactors..")
            .layer(REPOSITORIES).definedBy("..repositories..")
            .layer(DATASOURCES).definedBy("..datasources..")
            .layer(TRANSPORT_LAYERS).definedBy("..transportlayers..")
            .layer(CONFIGS).definedBy("..configs..")
            .whereLayer(INTERACTORS).mayOnlyBeAccessedByLayers(TRANSPORT_LAYERS,CONFIGS)
            .whereLayer(REPOSITORIES).mayOnlyBeAccessedByLayers(INTERACTORS, DATASOURCES, CONFIGS)
            .whereLayer(DATASOURCES).mayOnlyBeAccessedByLayers(CONFIGS)
            .whereLayer(TRANSPORT_LAYERS).mayOnlyBeAccessedByLayers(CONFIGS)
            .whereLayer(CONFIGS).mayOnlyBeAccessedByLayers(TRANSPORT_LAYERS, INTERACTORS, DATASOURCES);
}
