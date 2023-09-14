package com.readreview.changestream.service;

import com.mongodb.client.*;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.FullDocument;
import com.readreview.changestream.repository.AcessosRepository;
import com.readreview.changestream.domain.Acessos;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@Service
@Slf4j
public class MongoService {

    /* Este exemplo esta sendo usado o Shell do MondoDB e nao O Java driver sync com o spring data mongo utilizando o mongoTemplate.
    Este exemplo e para aplicacoes pequenas e simples. Para um sistema mais robusto o sugerido e utilizar o java driver.
    */
    @EventListener(ApplicationReadyEvent.class)
    /*
    O ApplicationReadyEvent é um evento que é disparado quando o aplicativo Spring está completamente inicializado
    e pronto para lidar com solicitações ou executar outras tarefas. Isso geralmente acontece depois que todos os
    beans são criados e configurados, e o aplicativo está pronto para uso.

    A anotação @EventListener permite que você associe métodos a eventos específicos, para que esses métodos sejam
    chamados automaticamente quando o evento ocorrer.

    @PostConstruction tambem pode ser usado neste caso, o problema do postconstruct ele realiza qualquer inicialização
    personalizada necessária para o bean antes que ele comece a executar seu comportamento principal.
    Por exemplo, você pode usar o @PostConstruct para configurar recursos, abrir conexões de banco de dados, iniciar
    tarefas em segundo plano, entre outras ações de inicialização. Desta forma, a aplicacao nao e iniciada por completa.
     */
    public void testMongo(){

        MongoClient mongoClient = MongoClients.create("mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+1.10.5");
        /* O mondoDB utilizando o sistema changeStreams so funciona em uma conexao com replicaSet (ver Deploying a MongoDB Cluster with Docker)
        em uma conexao normal utilizando localhost:27017 nao ira funcionar.
        */

        MongoDatabase db = mongoClient.getDatabase("meubanco");
        MongoCollection<Document> acessos = db.getCollection("acessos");

        Document doc = acessos.find(eq("name", "Test1")).first();
        if (doc != null) {
            System.out.println(doc.toJson());
        } else {
            System.out.println("No matching documents found.");
        }

        List<Bson> pipeline = Arrays.asList(
                Aggregates.match(
                        Filters.in(
                                "operationType",
                                Arrays.asList("insert", "update", "delete")
                        )
                )
        );

        ChangeStreamIterable<Document> changeStreams = acessos.watch(pipeline).fullDocument(FullDocument.UPDATE_LOOKUP);

        /* Cria um changeStream e inicia o monitoramento com o Watch de acordo o que foi passado na pipeline
        Enviando um fullDocument (sem filtros) */


        for (ChangeStreamDocument<Document> changeEvent : changeStreams){
            log.info(" Logs ={} ", changeEvent);

            /* para da changeStream guarde em um changeEvent do tipo ChangeStreamDocument */


        }

    }
}
