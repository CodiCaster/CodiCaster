package com.ll.codicaster.boundedContext.article.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBoard is a Querydsl query type for Board
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBoard extends EntityPathBase<Article> {

    private static final long serialVersionUID = 282810639L;

    public static final QBoard board = new QBoard("board");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QBoard(String variable) {
        super(Article.class, forVariable(variable));
    }

    public QBoard(Path<? extends Article> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBoard(PathMetadata metadata) {
        super(Article.class, metadata);
    }

}

