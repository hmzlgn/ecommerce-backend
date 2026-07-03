package com.example.demo.Category;

import lombok.Data;

@Data
public class CategoryCreateDto {
    private String name;

    //Eğer alt kategoriyse dolu olacak. Aksi halde null.
    private Long parentId;
}
