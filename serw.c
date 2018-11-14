#include <sys/types.h>
#include <sys/wait.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <netdb.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <pthread.h>

#define SERVER_PORT 1234
#define QUEUE_SIZE 5

//struktura zawierająca dane, które zostaną przekazane do wątku
struct thread_data_t
{
char buf[100];
int socket;

};
int sockets[2] = {1,1};

//funkcja opisującą zachowanie wątku - musi przyjmować argument typu (void *) i zwracać (void *)
void *ThreadBehavior1(void *t_data)
{
    pthread_detach(pthread_self());
    struct thread_data_t *th_data = (struct thread_data_t*)t_data;
    memset((*th_data).buf, 0, sizeof (*th_data).buf);
    while (read( (*th_data).socket, (*th_data).buf, 100) > 0){
      int i = (*th_data).socket;
      for(int j = 0; j < 2; j++){
          if(i!=sockets[j]) write(sockets[j], (*th_data).buf, 100);
      }
    }
    close((*th_data).socket);
    free(th_data);
    pthread_exit(NULL);
}


//funkcja obsługująca połączenie z nowym klientem
void handleConnection(int csd) {
    //wynik funkcji tworzącej wątek
    printf("%d\n", csd);
    
    pthread_t thread;
    struct thread_data_t * t_data = malloc(sizeof(struct thread_data_t));
    (*t_data).socket = csd;
    pthread_create(&thread, NULL, ThreadBehavior1, t_data);
}

int main(int argc, char* argv[])
{
   int server_socket_descriptor;
   int connection_socket_descriptor;
   int bind_result;
   int listen_result;
   char reuse_addr_val = 1;
   struct sockaddr_in server_address;

   //inicjalizacja gniazda serwera
   memset(&server_address, 0, sizeof(struct sockaddr));
   server_address.sin_family = AF_INET;
   server_address.sin_addr.s_addr = htonl(INADDR_ANY);
   server_address.sin_port = htons(1337);

   server_socket_descriptor = socket(AF_INET, SOCK_STREAM, 0);
   if (server_socket_descriptor < 0)
   {
       fprintf(stderr, "%s: Błąd przy próbie utworzenia gniazda..\n", argv[0]);
       exit(1);
   }
   setsockopt(server_socket_descriptor, SOL_SOCKET, SO_REUSEADDR, (char*)&reuse_addr_val, sizeof(reuse_addr_val));

   bind_result = bind(server_socket_descriptor, (struct sockaddr*)&server_address, sizeof(struct sockaddr));
   if (bind_result < 0)
   {
       fprintf(stderr, "%s: Błąd przy próbie dowiązania adresu IP i numeru portu do gniazda.\n", argv[0]);
       exit(1);
   }

   listen_result = listen(server_socket_descriptor, 100);
   if (listen_result < 0) {
       fprintf(stderr, "%s: Błąd przy próbie ustawienia wielkości kolejki.\n", argv[0]);
       exit(1);
   }
   int i = 0;
   while(1)
   {
       printf("start petli\n");
       connection_socket_descriptor = accept(server_socket_descriptor, NULL, NULL);
       sockets[i++] = connection_socket_descriptor;
       printf("%d %d\n", sockets[0], sockets[1]);
       handleConnection(connection_socket_descriptor);
       if (i == 2) i = 0;
       
   }

   close(server_socket_descriptor);
   return(0);
}
