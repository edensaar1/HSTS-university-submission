# HSTS design documentation

The HSTS design phase translated the academic requirements into BCE-oriented class and sequence models. The selected artifacts below represent the final design set used during implementation, while the Java source remains authoritative where the implementation later evolved.

| Diagram | What it represents | Full PDF | Preview |
| --- | --- | --- | --- |
| System class diagram | A broad view of boundaries, controls, server services, repositories, entities, and their relationships. It also contains some planned classes that were not retained in the final code. | [PDF](hsts-class-diagram.pdf) | [PNG](previews/hsts-class-diagram.png) |
| Exam-creation sequence | The designed interaction from the teacher screen through client/server controls and repositories. The final application retained teacher-selected question creation but not the automatic-generation alternative shown here. | [PDF](exam-creation-sequence.pdf) | [PNG](previews/exam-creation-sequence.png) |
| Student grade-viewing sequence | The designed retrieval path for student courses and approved examination results. | [PDF](watch-grades-sequence.pdf) | [PNG](previews/watch-grades-sequence.png) |

The PDFs are preserved at their original visual quality. The PNG files are scaled previews for convenient browsing; use the PDF links to inspect dense details.

No names, student identifiers, email addresses, credentials, local paths, or other personal information were visible in the selected diagrams. Nonessential source metadata was removed from these repository copies.

HSTS was developed as a university Software Engineering project. Its requirements and UML design phase involved collaborative academic work, and the application integrates the course-provided OCSF networking foundation.

[Return to the project README](../../README.md)
