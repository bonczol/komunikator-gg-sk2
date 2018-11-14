#include <pthread.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <stdio.h>

#define BUF_SIZE 1024
#define NUM_THREADS     5

//struktura zawierająca dane, które zostaną przekazane do wątku
pthread_mutex_t example_mutex = PTHREAD_MUTEX_INITIALIZER;
struct thread_data_t
{
char buf[100];
int socket;
};

//wskaźnik na funkcję opisującą zachowanie wątku
void *ThreadBehavior1(void *t_data)
{   
    //wypisywanie
    struct thread_data_t *th_data = (struct thread_data_t*)t_data;
    //dostęp do pól struktury: (*th_data).pole
    //TODO (przy zadaniu 1) klawiatura -> wysyłanie albo odbieranie -> wyświetlanie
    
        if( read( (*th_data).socket, (*th_data).buf, 100) > 0){ 
            printf("%s", (*th_data).buf);
            memset((*th_data).buf, 0, sizeof((*th_data).buf));
        }
    
    pthread_exit(NULL);
}

void *ThreadBehavior2(void *t_data)
{   
    //czytanie
    struct thread_data_t *th_data = (struct thread_data_t*)t_data;
    //dostęp do pól struktury: (*th_data).pole
    //TODO (przy zadaniu 1) klawiatura -> wysyłanie albo odbieranie -> wyświetlanie
        //printf("%d 2\n", (*th_data).socket);

    memset((*th_data).buf, 0, sizeof((*th_data).buf));
    scanf("%s", (*th_data).buf);
    write((*th_data).socket, (*th_data).buf, 100);
    pthread_exit(NULL);
}




//funkcja obsługująca połączenie z serwerem
void handleConnection(int connection_socket_descriptor) {
    //wynik funkcji tworzącej wątek
    int create_result = 0;
    //uchwyt na wątek
    
    
    while(1){
      pthread_t thread1;
      //dane, które zostaną przekazane do wątku
      struct thread_data_t t_data1;
      t_data1.socket = connection_socket_descriptor;
      //printf("%d\n", t_data1.socket);
      create_result = pthread_create(&thread1, NULL, ThreadBehavior1, (void *)&t_data1);

      pthread_t thread2;
      struct thread_data_t t_data2;
      t_data2.socket = connection_socket_descriptor;
      create_result = pthread_create(&thread2, NULL, ThreadBehavior2, (void *)&t_data2);
    }
    
    //TODO (przy zadaniu 1) odbieranie -> wyświetlanie albo klawiatura -> wysyłanie
}


int main (int argc, char *argv[])
{
   int connection_socket_descriptor;
   int connect_result;
   struct sockaddr_in server_address;
   struct hostent* server_host_entity;

   if (argc != 3)
   {
     fprintf(stderr, "Sposób użycia: %s server_name port_number\n", argv[0]);
     exit(1);
   }

   server_host_entity = gethostbyname(argv[1]);
   if (! server_host_entity)
   {
      fprintf(stderr, "%s: Nie można uzyskać adresu IP serwera.\n", argv[0]);
      exit(1);
   }

   connection_socket_descriptor = socket(AF_INET, SOCK_STREAM, 0);
   if (connection_socket_descriptor < 0)
   {
      fprintf(stderr, "%s: Błąd przy probie utworzenia gniazda.\n", argv[0]);
      exit(1);
   }

   memset(&server_address, 0, sizeof(struct sockaddr));
   server_address.sin_family = AF_INET;
   memcpy(&server_address.sin_addr.s_addr, server_host_entity->h_addr, server_host_entity->h_length);
   server_address.sin_port = htons(atoi(argv[2]));

   connect_result = connect(connection_socket_descriptor, (struct sockaddr*)&server_address, sizeof(struct sockaddr));
   if (connect_result < 0)
   {
      fprintf(stderr, "%s: Błąd przy próbie połączenia z serwerem (%s:%i).\n", argv[0], argv[1], atoi(argv[2]));
      exit(1);
   }

   handleConnection(connection_socket_descriptor);

   close(connection_socket_descriptor);
   return 0;

}
